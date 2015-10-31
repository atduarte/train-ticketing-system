<?php

namespace AppBundle\Service;


use AppBundle\Document\Ticket;
use AppBundle\Document\Trip;
use AppBundle\Document\User;
use Doctrine\ODM\MongoDB\DocumentManager;

class TrainManager
{
    /**
     * @var DocumentManager
     */
    private $documentManager;
    /**
     * @var TrainInformation
     */
    private $trainInformation;
    /**
     * @var CreditCardValidator
     */
    private $creditCardValidator;

    /**
     * @param DocumentManager $documentManager
     * @param TrainInformation $trainInformation
     * @param CreditCardValidator $creditCardValidator
     */
    public function __construct(DocumentManager $documentManager, TrainInformation $trainInformation, CreditCardValidator $creditCardValidator)
    {
        $this->documentManager = $documentManager;
        $this->trainInformation = $trainInformation;
        $this->creditCardValidator = $creditCardValidator;
    }

    public function getDailyTrips($fromName, $toName, $date)
    {
        $possibilities = [];
        $trips = $this->trainInformation->getTrip($fromName, $toName);

        // Get Capacities
        foreach ($trips as &$trip) {
            foreach ($trip['times'] as $i => &$time) {
                $time['capacity'] = $this->getCapacity(
                    $date,
                    $trip['lineNumber'],
                    $trip['from'],
                    $trip['to'],
                    $trip['lineTimes'][$i]['departure']
                );

                // Remove time if no capacity
                if (!$time['capacity']) {
                    unset($trip['times'][$i]);
                    unset($trip['lineTimes'][$i]);
                }
            }
            unset($time);

            $trip['times'] = array_values($trip['times']);
            $trip['lineTimes'] = array_values($trip['lineTimes']);
        }
        unset($trip);

        $transformTrip = function ($trip, $timeIndex) {
            $trip['times'] = $trip['times'][$timeIndex];
            $trip['capacity'] = $trip['times']['capacity'];
            unset($trip['times']['capacity']);
            $trip['lineTimes'] = $trip['lineTimes'][$timeIndex];

            return $trip;
        };

        // Make Matching Trips

        if (count($trips) == 0) {
            return $possibilities;

        } else if (count($trips) == 1) {
            foreach ($trips[0]['times'] as $i => $time) {
                $possibilities[] = [$transformTrip($trips[0], $i)];
            }

        } else if (count($trips) == 2) {
            foreach ($trips[0]['times'] as $i => $firstTime) {
                foreach ($trips[1]['times'] as $j => $secondTime) {
                    if ($firstTime['arrival'] < $secondTime['departure']) {
                        $possibilities[] = [
                            $transformTrip($trips[0], $i),
                            $transformTrip($trips[1], $j)
                        ];
                    }
                }
            }
        }


        // List of combinations of possible trips
//        $resultExample = [
//            [
//                [
//                    'lineNumber' => 0,
//                    'lineStations' => [['name' => 'X', 'km' => 0]], // all line stations
//                    'from' => 0,
//                    'to' => 2,
//                    'duration' => 0,
//                    'lineDuration' => 1,
//                    'times' => ['departure' => 0, 'arrival' => 1, 'capacity' => 5],
//                    'lineTimes' => ['departure' => 0, 'arrival' => 1, 'capacity' => 5],
//                ],
//                [
//                    'lineNumber' => 1,
//                    'lineStations' => [['name' => 'X', 'km' => 0]], // all line stations
//                    'from' => 0,
//                    'to' => 2,
//                    'duration' => 0,
//                    'lineDuration' => 1,
//                    'times' => ['departure' => 1, 'arrival' => 2, 'capacity' => 5],
//                    'lineTimes' => ['departure' => 1, 'arrival' => 2, 'capacity' => 5],
//                ],
//            ],
//            // ...
//        ];

        return  $possibilities;
    }

    /**
     * @param $user
     * @param $date
     * @param $lineNumber
     * @param $from
     * @param $to
     * @param $lineDeparture
     * @return Ticket|null
     */
    public function buyTicket(User $user, $date, $lineNumber, $from, $to, $lineDeparture)
    {
        // Get Trip
        $trip = $this->documentManager->getRepository('AppBundle:Trip')
            ->findOneBy(['date' => $date, 'lineNumber' => (int)$lineNumber, 'departure' => (int)$lineDeparture]);

        // If doesn't exists
        if (!$trip) {
            // Check lineNumber | from | to | lineDeparture
            if (!$line = $this->trainInformation->verifyLine($lineNumber, $from, $to, $lineDeparture)) {
                throw new \RuntimeException('Invalid trip.');
            }

            // Get Train capacity
            if ($this->getCapacity($date, $lineNumber, $from, $to, $lineDeparture) == 0) {
                throw new \RuntimeException('Trip is full.');
            }

            // Create Trip
            $trip = new Trip(
                $lineNumber,
                $line['stations'],
                $date,
                $lineDeparture,
                $this->trainInformation->getCapacity()
            );
        }

        if (!$this->creditCardValidator->validate($user->getCreditCard())) {
            throw new \RuntimeException('Credit Card failed.');
        }

        // Create Ticket
        $ticket = new Ticket($user, $trip, $from, $to);

        // Update Trip
        $trip->addTicketBought($from, $to);

        // Persist
        $this->documentManager->persist($ticket);
        $this->documentManager->persist($trip);
        $this->documentManager->flush();

        return $ticket;
    }

    public function getCapacity($date, $lineNumber, $from, $to, $lineDeparture)
    {

        /** @var null|Trip $trip */
        $trip = null; // TODO:  Search for the Trip Object

        $capacity = $trip ? $trip->getAvailableCapacity($from, $to) : $this->trainInformation->getCapacity();

        return $capacity;
    }

    protected function getTrip($date, $lineNumber, $from, $to, $lineDeparture)
    {

    }
}