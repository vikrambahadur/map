# map - Simple message processing application https://github.com/vikrambahadur/map

## How to run
Required Java version is 1.8,
Class https://github.com/vikrambahadur/map/blob/master/src/jpmc/test/mpa/app/SalesMessageServerPublisher.java  works as web service server (run this class) and publish the messaging client interface service to end point http://localhost:9999/ws/sales, any soap base client can be used to import the wsdl http://localhost:9999/ws/sales?wsdl and sending the messages to server


## Unit Testing
class SalesMessageTest location https://github.com/vikrambahadur/map/tree/master/unittest/jpmc/test/mpa/core/model
serve all the basic message format validation to message processing unit testing

