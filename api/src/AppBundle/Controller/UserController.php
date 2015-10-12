<?php

namespace AppBundle\Controller;

use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations\Post;

class UserController extends BaseController
{
    /**
     * @Post("/login")
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\Response
     */
    public function loginAction(Request $request)
    {
        // TODO

        $jwtEncoder = $this->get('lexik_jwt_authentication.jwt_encoder');
        $jwt = $jwtEncoder->encode(['email' => 'mail@andreduarte.net']);

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
        // TODO
    }
}
