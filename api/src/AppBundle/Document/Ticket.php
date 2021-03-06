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

    private $date;

    public function __construct($user, Trip $trip, $from, $to)
    {
        $this->user = $user;
        $this->trip = $trip;
        $this->date = $trip->getDate();
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
        $ticket = [
            'id' => $this->getId(),
            'lineNumber' => $this->getTrip()->getLineNumber(),
            'date' => $this->getTrip()->getDate()->format('Y-m-d'),
            'from' => $this->getTrip()->getStations()[(int)$this->from]['name'],
            'to' => $this->getTrip()->getStations()[(int)$this->to]['name'],
            'departure' => $this->getTrip()->getDeparture()
        ];

        $fp = fopen(__DIR__ . '/../../../app/Resources/id_rsa', 'r');
        $res = openssl_get_privatekey(fread($fp, 8192));
        fclose($fp);

        $toSign = $ticket['id'] . $ticket['lineNumber'] . $ticket['from'] . $ticket['to'] . $ticket['date'] . $ticket['departure'];
        openssl_sign($toSign, $signature, $res);
        $ticket['signature'] = base64_encode($signature);

        return $ticket;
    }


}