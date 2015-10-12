<?php

namespace AppBundle\Controller;

use AppBundle\Document\User;
use Doctrine\Common\Proxy\Exception\InvalidArgumentException;
use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations\Post;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;

class UserController extends BaseController
{
    /**
     * @Post("/login")
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\Response
     */
    public function loginAction(Request $request)
    {
        $email = $request->request->get('email');
        $password = $request->request->get('password');

        $dm = $this->get('doctrine.odm.mongodb.document_manager');

        /** @var User $user */
        $user = $dm->getRepository('AppBundle:User')->findOneBy([
            'email' => $email
        ]);

        if (!$user || !$user->checkPassword($password)) {
            throw new AccessDeniedException();
        }

        $jwtEncoder = $this->get('lexik_jwt_authentication.jwt_encoder');
        $jwt = $jwtEncoder->encode(['email' => $user->getEmail()]);

        return [
            'token' => $jwt
        ];
    }

    /**
     * @Post("/register")
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\Response
     */
    public function registerAction(Request $request)
    {
        $email = $request->request->get('email');
        $password = $request->request->get('password');
        $confirmationPassword = $request->request->get('confirmation_password');

        if (!$email || !$password) {
            throw new \Symfony\Component\Validator\Exception\InvalidArgumentException('Email and password required');
        }

        if ($password !== $confirmationPassword) {
            throw new \Symfony\Component\Validator\Exception\InvalidArgumentException('Passwords (password and confirmation_password) are different');
        }

        $dm = $this->get('doctrine.odm.mongodb.document_manager');

        $user = $dm->getRepository('AppBundle:User')->findOneBy([
            'email' => $email
        ]);

        if ($user) {
            throw new \Symfony\Component\Validator\Exception\InvalidArgumentException('User already exists');
        }

        $user = new User();
        $user->setEmail($email);
        $user->setPassword($password);

        $dm->persist($user);
        $dm->flush();

        return true;
    }
}
