<?php

namespace AppBundle\Controller;

use AppBundle\Document\User;
use FOS\RestBundle\Controller\FOSRestController;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\DependencyInjection\ContainerInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Security\Core\Exception\AccessDeniedException;

class BaseController extends Controller
{
    const LOGIN_TTL = 2592000; // 30 days

    protected function requireUserRole(Request $request)
    {
        $user = $this->getJWTUser($request);

        if (!$user) {
            throw new AccessDeniedException();
        }
    }

    protected function requireInspectorRole(Request $request)
    {
        $user = $this->getJWTUser($request);

        if (!$user || !$user->isInspector()) {
            throw new AccessDeniedException();
        }
    }

    /**
     * @param Request $request
     * @return bool|User
     */
    protected function getJWTUser(Request $request)
    {
        $jwtEncoder = $this->get('lexik_jwt_authentication.jwt_encoder');
        $jwt = $jwtEncoder->decode($request->headers->get('Authorization'));

        if (!isset($jwt['email']) || !$jwt['email']) {
            return false;
        }

        $dm = $this->get('doctrine.odm.mongodb.document_manager');

        /** @var User $user */
        $user = $dm->getRepository('AppBundle:User')->findOneBy([
            'email' => $jwt['email']
        ]);

        return $user;
    }

}
