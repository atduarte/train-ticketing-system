<?php

namespace AppBundle\Controller;

use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations\Get;

class TicketsController extends BaseController
{
    /**
     * @Get("/example")
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\Response
     */
    public function exampleAction(Request $request)
    {
        $this->requireUserRole($request);

        // TODO
    }
}
