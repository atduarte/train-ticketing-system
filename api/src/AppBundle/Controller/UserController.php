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
        $role = $request->request->get('role');

        $dm = $this->get('doctrine.odm.mongodb.document_manager');

        $query = ['email' => $email];

        if ($role === 'inspector') {
            $query['isInspector'] = true;
        }

        /** @var User $user */
        $user = $dm->getRepository('AppBundle:User')->findOneBy($query);

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
        $creditCard = [
            'name' => $request->request->get('cc-name'),
            'number' => $request->request->get('cc-number'),
            'cvc' => $request->request->get('cc-cvc'),
            'month' => $request->request->get('cc-month'),
            'year' => $request->request->get('cc-year'),
        ];

        if (!$email || !$password) {
            throw new \Symfony\Component\Validator\Exception\InvalidArgumentException('Email and password required');
        }

        if (!$this->get('credit_card_validator')->validate($creditCard)) {
            throw new \Symfony\Component\Validator\Exception\InvalidArgumentException('Invalid Credit Card given');
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
