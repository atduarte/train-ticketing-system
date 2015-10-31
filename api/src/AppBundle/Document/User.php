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
class User
{
    /** @MongoDB\Id */
    protected $id;

    /** @MongoDB\Field(type="string") */
    private $email;

    /** @MongoDB\Field(type="string") */
    private $hash;

    /** @MongoDB\Field(type="hash") */
    private $creditCard;

    /** @MongoDB\Field(type="boolean") */
    private $isInspector;


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
    public function getEmail()
    {
        return $this->email;
    }

    /**
     * @param mixed $email
     */
    public function setEmail($email)
    {
        $this->email = $email;
    }

    /**
     * @param $password
     * @return boolean
     */
    public function checkPassword($password)
    {
        return password_verify($password, $this->hash);
    }

    /**
     * @param mixed $hash
     */
    public function setPassword($hash)
    {
        $this->hash = password_hash($hash, PASSWORD_BCRYPT);
    }

    /**
     * @param mixed $isInspector
     * @return User
     */
    public function setIsInspector($isInspector)
    {
        $this->isInspector = $isInspector;
        return $this;
    }

    /**
     * @return mixed
     */
    public function isInspector()
    {
        return $this->isInspector;
    }

    /**
     * @return mixed
     */
    public function getCreditCard()
    {
        return $this->creditCard;
    }

    /**
     * @param mixed $creditCard
     */
    public function setCreditCard($creditCard)
    {
        $this->creditCard = $creditCard;
    }
}