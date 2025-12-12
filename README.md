
ngr-dashboard-frontend
================

## Nomenclature


## Technical documentation


### Before running the app (if applicable)

Ensure that you have the latest versions of the required services and that they are running. This can be done via service manager using the NGR_ALL profile.
```
sm2 --start NGR_ALL
sm2 --stop  NGR_DASHBOARD_FRONTEND
```
### Run local changes:
* `cd` to the root of the project.
* `sbt run`
* `Note` the service will run on port 1503 by default
* `Setup your policies:`
    *  make sure `centralised-authorisation-policy-config` is running `sbt run`
    *  run the shell script `runMainPolicyJsonGenerator.sh` found in the `centralised-authorisation-policy-config` repo
    *  stop `CENTRALISED_AUTHORISATION_POLICY_SERVER` in `service manager`
    *  start `CENTRALISED_AUTHORISATION_POLICY_SERVER` in `service manager`
    * 
### Run with test only routes to populate stub data:
* A dedicated test-only endpoint is available to populate stub data for development and testing purposes:
* Visit http://localhost:1503/test-only/populate-stub-data in your browser to trigger the stub data population.
* It will clear all existing stub data from the `ngr-stub` service's MongoDB collection and reload it with predefined data from `ngr-stu/conf/stubData`.
* To use this endpoint locally, run the following command:
   
      `sbt run -Dapplication.router=testOnlyDoNotUseInAppConf.Routes -Dcentralised-authorisation-resource-client.filter.enabled=false`
    
  * This route is intended for testing purposes only and should not be used in production environments.
  
### Running the test suite
```
sbt clean coverage test coverageReport
```
### Further documentation

shuttering:
* `QA` https://catalogue.tax.service.gov.uk/shuttering-overview/frontend/qa?teamName=Non+Domestic+Rates+Reform+Prog.
* `STAGING` https://catalogue.tax.service.gov.uk/shuttering-overview/frontend/staging?teamName=Non+Domestic+Rates+Reform+Prog.

## Licence
This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").