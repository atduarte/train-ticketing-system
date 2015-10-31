<?php

namespace AppBundle\Controller;

use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations\Get;
use FOS\RestBundle\Controller\Annotations\Post;

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
        $date = $this->parseDate($request->request->get('date'));

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

        $ticket = $this->get('train_manager')->buyTicket($user, $date, $lineNumber, $from, $to, $lineDeparture);

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
                ->findBy(['user.$id' => new \MongoId($this->user->getId())])
        );
    }

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
