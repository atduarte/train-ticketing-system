<?php

namespace AppBundle\Service;


/**
 * Class CreditCardValidator
 * @package AppBundle\Service
 */
class CreditCardValidator
{
    public function __construct()
    {

    }

    public function validate($creditCard)
    {
        $fields = ['number', 'name', 'cvc', 'month', 'year'];

        // Verify all fields exist
        foreach ($fields as $field) {
            if (!isset($creditCard[$field]) || empty($creditCard[$field]) || !is_string($creditCard[$field])) {
                return false;
            }
        }

        if (strlen($creditCard['number']) != 17) { return false; }

        if (strlen($creditCard['cvc']) != 3) { return false; }

        if ($creditCard['month'] < 1 || $creditCard['month'] > 12) { return false; }

        // Valid today?
        $year = (int)date('y'); $month = (int)date('n');
        if ($creditCard['year'] < $year || ($creditCard['year'] == $year && $creditCard['month'] <= $month)) {
            return false;
        }

        if ((rand(0, 9)+1)/10 <= 0.1) {
            return false;
        }

        return true;
    }
}