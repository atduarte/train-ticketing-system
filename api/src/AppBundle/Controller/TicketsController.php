<?php

namespace AppBundle\Controller;

use Symfony\Component\HttpFoundation\Request;
use FOS\RestBundle\Controller\Annotations\Get;

class TicketsController extends BaseController
{
    /**
     * @Get("/tickets")
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\Response
     */
    public function getTicketsAction(Request $request)
    {
        $this->requireUserRole($request);

        $from = $request->query->get('from');
        $to = $request->query->get('to');
        $date = $request->query->get('date'); // TODO: Convert to Mongo Date e piças

        return $this->get('train_manager')->getDailyTrips($from, $to, null);
    }
}
