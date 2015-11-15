<?php

namespace AppBundle\Controller;

use AppBundle\Document\Ticket;
use Symfony\Component\Form\Exception\InvalidArgumentException;
use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations\Get;
use FOS\RestBundle\Controller\Annotations\Post;
use Symfony\Component\Routing\Exception\InvalidParameterException;
use Symfony\Component\Validator\Constraints\DateTime;

class TicketsController extends BaseController
{
    /**
     * @Get("/stations")
     * @param Request $request
     * @return array
     */
    public function getStationsAction(Request $request)
    {
        $this->requireUserRole($request);

        $lines = $this->get('train_information')->getLines();
        $stations = [];

        foreach ($lines as $line) {
            $stations = array_merge($stations, array_column($line['stations'], 'name'));
        }

        $stations = array_values(array_unique($stations));
        return ['stations' => $stations];
    }

    /**
     * @Get("/tickets")
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\Response
     */
    public function getAvailableTicketsAction(Request $request)
    {
        $this->requireUserRole($request);

        $from = $request->query->get('from');
        $to = $request->query->get('to');
        $date = $this->parseDate($request->query->get('date'));

        return $this->get('train_manager')->getDailyTrips($from, $to, $date);
    }

    /**
     * @Post("/ticket")
     * @param Request $request
     * @return array
     * @throws \Exception
     */
    public function buyTicketAction(Request $request)
    {
        $this->requireUserRole($request);

        $user = $this->user;
        $lineNumber = $request->request->get('lineNumber');
        $lineDeparture = $request->request->get('lineDeparture');
        $from = $request->request->get('from');
        $to = $request->request->get('to');
        $date = $this->parseDate($request->request->get('date'));
        $continuation = $request->request->get('continuation', 0);

        $ticket = $this->get('train_manager')->buyTicket($user, $date, $lineNumber, $from, $to, $lineDeparture, $continuation);

        if (!$ticket) {
            throw new \Exception('Couldn\'t buy the ticket');
        }

        return $ticket->toArray();
    }

    /**
     * @Get("/my-tickets")
     * @param Request $request
     * @return array
     */
    public function getBoughtTicketsAction(Request $request)
    {
        $this->requireUserRole($request);

        return array_map(
            function ($ticket) { return $ticket->toArray(); },
            $this->get('doctrine.odm.mongodb.document_manager')->getRepository('AppBundle:Ticket')
                ->findBy(['user.$id' => new \MongoId($this->user->getId())], ['date' => -1])
        );
    }

    /**
     * @Get("/lines")
     * @param Request $request
     * @return array
     */
    public function getLinesAction(Request $request)
    {
        $this->requireInspectorRole($request);

        $lines = $this->get('train_information')->getLines();

        return array_map(function ($line) {
            $times = [];

            foreach ($line['departures'] as $departure) {
                $times[] = [$departure, $this->get('train_information')->getCapacity()];
            }

            return [
                'lineNumber' => $line['number'],
                'name' => $line['stations'][0]['name'] . ' - ' . end($line['stations'])['name'],
                'times' => $times
            ];
        }, $lines);
    }

    /**
     * @Get("/timetable")
     * @param Request $request
     * @return array
     */
    public function getTimetableAction(Request $request)
    {
        $this->requireUserRole($request);

        $lines = $this->get('train_information')->getLines();

        return array_map(function ($rawLine) {
            $line = [
                'line' => $rawLine['number'],
                'from' => $rawLine['stations'][0]['name'],
                'to' => end($rawLine['stations'])['name'],
                'timetables' => []
            ];

            foreach ($rawLine['departures'] as $departureTime) {
                $timetable = [
                    'departure' => $departureTime,
                    'arrival' => $departureTime + $rawLine['duration'],
                    'stations' => []
                ];

                $totalKm = end($rawLine['stations'])['km'];
                foreach ($rawLine['stations'] as $station) {
                    $timetable['stations'][] = [
                        'name' => $station['name'],
                        'departure' => ceil($departureTime + (($station['km'] / $totalKm) * $rawLine['duration']))
                    ];
                }

                $line['timetables'][] = $timetable;
            }

            return $line;
        }, $lines);
    }

//    /**
//     * @param Request $request
//     * @return array
//     */
//    public function getTicketsForValidationAction(Request $request)
//    {
//        $this->requireInspectorRole($request);
//
//        $lineNumber = $request->query->get('line');
//        $departure = $request->query->get('departure');
//
//        if (!is_numeric($lineNumber) || !is_numeric($departure)) {
//            throw new InvalidArgumentException('Line Number ("line") and Departure ("departure") query parameters are required and should be numeric.');
//        }
//
//        // Get Trip
//        $date = new \DateTime();
//        $date->setTime(0,0);
//        $trip = $this->get('doctrine.odm.mongodb.document_manager')->getRepository('AppBundle:Trip')
//            ->findOneBy(['date' => new \MongoDate($date->getTimestamp())]);
//
//        // Get Tickets(
//        $tickets = [];
//        if ($trip) {
//            $tickets = $this->get('doctrine.odm.mongodb.document_manager')->getRepository('AppBundle:Ticket')
//                ->findBy(['trip.$id' => new \MongoId($trip->getId())]);
//        }
//
//        return array_map(function (Ticket $ticket) { return $ticket->toArray(); }, $tickets);
//    }

    protected function parseDate($date)
    {
        $date = \DateTime::createFromFormat('Y-m-d', $date);
        $errors = \DateTime::getLastErrors();

        if ($errors['error_count'] + $errors['warning_count'] > 0) {
            throw new \RuntimeException('Invalid date');
        }

        $date->setTime(0,0,0);
        return $date;
    }
}
