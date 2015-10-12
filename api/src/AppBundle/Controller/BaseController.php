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

    public function requireUserRole(Request $request)
    {
        $user = $this->getJWTUser($request);

        if (!$user) {
            throw new AccessDeniedException();
        }
    }

    public function requireInspectorRole(Request $request)
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
    private function getJWTUser(Request $request)
    {
        // TODO
        return false;
    }

}
