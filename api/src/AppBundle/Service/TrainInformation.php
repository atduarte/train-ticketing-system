<?php

namespace AppBundle\Service;


/**
 * Class CreditCardValidator
 * @package AppBundle\Service
 */
class TrainInformation
{
    protected $capacity;
    protected $lines = [
        [
            'number' => 0,
            'stations' => [
                ['name' => 'Algarve', 'km' => 0],
                ['name' => 'Lisboa',  'km' => 300],
                ['name' => 'Coimbra', 'km' => 500],
                ['name' => 'Aveiro',  'km' => 550],
                ['name' => 'Porto',   'km' => 600]
            ],
            'duration' => 210,
            'departures' => [0, 480, 960],
            // Coimbra -> 175
        ],
        [
            'number' => 1,
            'stations' => [
                ['name' => 'Porto', 'km' => 0],
                ['name' => 'Aveiro', 'km'  => 50],
                ['name' => 'Coimbra', 'km'  => 100],
                ['name' => 'Lisboa', 'km'  => 300],
                ['name' => 'Algarve', 'km'  => 600]
            ],
            'duration' => 210,
            'departures' => [0, 480, 960],
            // Coimbra -> 35
        ],
        [
            'number' => 2,
            'stations' => [
                ['name' => 'Bragança', 'km' => 0],
                ['name' => 'Viseu', 'km' => 50],
                ['name' => 'Coimbra', 'km' => 100],
            ],
            'duration' => 60,
            'departures' => [0, 480, 960],
            // Coimbra -> 100
        ],
        [
            'number' => 3,
            'stations' => [
                ['name' => 'Coimbra', 'km' => 0],
                ['name' => 'Viseu', 'km' => 50],
                ['name' => 'Bragança', 'km' => 100],
            ],
            'duration' => 60,
            'departures' => [0, 480, 960],
            // Coimbra -> 0
        ],
    ];

    public function getCapacity()
    {
        return 10;
    }

    /**
     * @param $fromName
     * @param $toName
     * @return array - Array of trips, even if only one needed
     */
    public function getTrip($fromName, $toName)
    {
        $trips = [];

        $pointer = $fromName;
        while ($pointer !== $toName) {
            $trip = $this->getPartialTrip($pointer, $toName);
            if (!$trip) return false;

            $trips[] = $trip;
            $pointer = $trip['lineStations'][$trip['to']]['name'];
        }

//        $example = [[
//            'lineNumber' => 0,
//            'lineStations' => [['name' => 'X', 'km' => 0]], // all line stations
//            'from' => 0,
//            'to' => 2,
//            'duration' => 0,
//            'lineDuration' => 1,
//            'times' => [
//                ['departure' => 0, 'arrival' => 1],
//                /* ... */
//            ],
//            'lineTimes' => [
//                ['departure' => 0, 'arrival' => 1],
//                /* ... */
//            ],
//        ]];

        return $trips;
    }

    protected function getPartialTrip($fromName, $toName)
    {
        foreach ($this->lines as $line) {
            $fromIndex = array_search($fromName, array_column($line['stations'], 'name'));
            if ($fromIndex === false) {
                continue;
            }

            $toIndex = array_search($toName, array_column($line['stations'], 'name'));
            if ($toIndex === false && $fromName !== 'Coimbra') {
                $toIndex = array_search('Coimbra', array_column($line['stations'], 'name'));
            }

            if ($fromIndex > $toIndex) {
                continue;
            }

            $trip = [
                'lineNumber' => $line['number'],
                'lineStations' => $line['stations'],
                'from' => $fromIndex,
                'to' => $toIndex,
                'fromName' => $line['stations'][$fromIndex]['name'],
                'toName' => $line['stations'][$toIndex]['name'],
                'lineDuration' => $line['duration'],
                'lineDistance' => end($line['stations'])['km']
            ];

            // Helper
            $minPerKm = $trip['lineDuration'] / $trip['lineDistance'];
            $fromKm = $trip['lineStations'][$trip['from']]['km'];
            $toKm = $trip['lineStations'][$trip['to']]['km'];

            // Calculate Trip Distance + Duration
            $trip['distance'] = $toKm - $fromKm;
            $trip['duration'] = $minPerKm * $trip['distance'];

            // Calculate Times
            foreach ($line['departures'] as $departure) {
                $trip['lineTimes'][] = ['departure' => $departure, 'arrival' => $departure + $trip['lineDuration']];
                $trip['times'][] = [
                    'departure' => $departure + $minPerKm * $fromKm,
                    'arrival' => $departure + $minPerKm * $toKm
                ];
            }

            return $trip;
        }

        return null;
    }

    public function getLines()
    {
        return $this->lines;
    }

    public function verifyLine($number, $from, $to, $departure)
    {
        foreach ($this->lines as $line) {
            $stationsNo = count($line['stations']);
            if ($number == $line['number'] && in_array($departure, $line['departures']) && $from < $to && $to < $stationsNo) {
                return $line;
            }
        }

        return false;
    }
}