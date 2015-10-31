<?php

namespace AppBundle\Document;

use Doctrine\ODM\MongoDB\Mapping\Annotations as MongoDB;
use FOS\UserBundle\Model\User as FOSUser;


/**
 * Class Client
 * @package AppBundle\Document
 *
 * @MongoDB\Document
 */
class Ticket
{
    /** @MongoDB\Id */
    protected $id;

    /** @MongoDB\ReferenceOne(targetDocument="AppBundle\Document\User") */
    private $user;

    /** @MongoDB\ReferenceOne(targetDocument="AppBundle\Document\Trip") */
    private $trip;

    /** @MongoDB\Field(type="integer") */
    private $from;

    /** @MongoDB\Field(type="integer") */
    private $to;

    public function __construct($user, $trip, $from, $to)
    {
        $this->user = $user;
        $this->trip = $trip;
        $this->from = $from;
        $this->to = $to;
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
    public function getUser()
    {
        return $this->user;
    }

    /**
     * @param mixed $user
     */
    public function setUser($user)
    {
        $this->user = $user;
    }

    /**
     * @return Trip
     */
    public function getTrip()
    {
        return $this->trip;
    }

    /**
     * @param mixed $trip
     */
    public function setTrip($trip)
    {
        $this->trip = $trip;
    }

    /**
     * @return mixed
     */
    public function getFrom()
    {
        return $this->from;
    }

    /**
     * @param mixed $from
     */
    public function setFrom($from)
    {
        $this->from = $from;
    }

    /**
     * @return mixed
     */
    public function getTo()
    {
        return $this->to;
    }

    /**
     * @param mixed $to
     */
    public function setTo($to)
    {
        $this->to = $to;
    }

    public function getCode()
    {
        return sha1((string)$this->id);
    }

    public function toArray()
    {
        return [
            'code' => $this->getCode(),
            'user' => $this->getUser()->getEmail(),
            'date' => $this->getTrip()->getDate()->getTimestamp(),
            'boughtAt' => (int)(new \MongoId($this->getId()))->getTimestamp(),
            'lineNumber' => (int)$this->getTrip()->getLineNumber(),
            'lineStations' => $this->getTrip()->getStations(),
            'from' => (int)$this->from,
            'to' => (int)$this->to
        ];
    }


}