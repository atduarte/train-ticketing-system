<?php

namespace AppBundle\Document;

use Doctrine\ODM\MongoDB\Mapping\Annotations as MongoDB;


/**
 * Class Client
 * @package AppBundle\Document
 *
 * @MongoDB\Document
 */
class Trip
{
    /** @MongoDB\Id */
    protected $id;

    /** @MongoDB\Integer */
    protected $lineNumber;

    /** @MongoDB\Hash */
    protected $stations; // Desnormalização para melhorar performance

    /** @MongoDB\Date */
    protected $date;

    /** @MongoDB\Integer */
    protected $departure;

    /** @MongoDB\Integer */
    protected $totalCapacity;

    /** @MongoDB\Hash */ // TODO ?
    protected $ticketsBought;

    public function __construct($lineNumber, $stations, $date, $departure, $totalCapacity)
    {
        $this->lineNumber = $lineNumber;
        $this->date = $date;
        $this->departure = $departure;
        $this->totalCapacity = $totalCapacity;
        $this->ticketsBought = array_fill(0, count($stations)-1, 0);
        $this->stations = $stations;
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @param mixed $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @return mixed
     */
    public function getLineNumber()
    {
        return $this->lineNumber;
    }

    /**
     * @param mixed $lineNumber
     */
    public function setLineNumber($lineNumber)
    {
        $this->lineNumber = $lineNumber;
    }

    /**
     * @return mixed
     */
    public function getDate()
    {
        return $this->date;
    }

    /**
     * @param mixed $date
     */
    public function setDate($date)
    {
        $this->date = $date;
    }

    /**
     * @return mixed
     */
    public function getDeparture()
    {
        return $this->departure;
    }

    /**
     * @param mixed $departure
     */
    public function setDeparture($departure)
    {
        $this->departure = $departure;
    }

    /**
     * @return mixed
     */
    public function getTotalCapacity()
    {
        return $this->totalCapacity;
    }

    /**
     * @param mixed $totalCapacity
     */
    public function setTotalCapacity($totalCapacity)
    {
        $this->totalCapacity = $totalCapacity;
    }

    /**
     * @return mixed
     */
    public function getTicketsBought()
    {
        return $this->ticketsBought;
    }

    public function addTicketBought($from, $to)
    {
        for ($i = $from; $i < $to; $i++) {
            $this->ticketsBought[$i]++;
        }
    }

    public function getAvailableCapacity($from, $to)
    {
        $capacities = [$this->totalCapacity];

        for ($i = $from; $i < $to; $i++) {
            $capacities[] = $this->totalCapacity - $this->ticketsBought[$i];
        }

        $capacity = min($capacities);
        return $capacity < 0 ? 0 : $capacity;
    }

    /**
     * @param mixed $ticketsBought
     */
    public function setTicketsBought($ticketsBought)
    {
        $this->ticketsBought = $ticketsBought;
    }

    /**
     * @return mixed
     */
    public function getStations()
    {
        return $this->stations;
    }

    /**
     * @param mixed $stations
     */
    public function setStations($stations)
    {
        $this->stations = $stations;
    }

}