/*
 * Copyright (c) eZuce, Inc. All rights reserved.
 * Contributed to SIPfoundry under a Contributor Agreement
 *
 * This software is free software; you can redistribute it and/or modify it under
 * the terms of the Affero General Public License (AGPL) as published by the
 * Free Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 */

(function(){
  'use strict';

  uw.factory('request', [
    '$http',
    function ($http) {
      /**
       * request generator with custom config
       * e.g.
       *     request({
       *       ...
       *       angular.js $http conf object
       *       ...
       *     })
       *
       * @param  {Object} conf    angular.js $http configuration object
       * @return {Object}         promise  response data || error
       */
      return function (conf) {
        return $http(conf).
          success(function(data) {
            return data;
          }).
          error(function(data, status, headers) {
            return new Error();
          });
      };

    }
  ]);
})();
