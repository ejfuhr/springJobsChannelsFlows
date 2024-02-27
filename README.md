# Kotlin, Spring, Channels, Flow and Mongo
* This project presents a review of how kotlin handles Flows and Channels through a few examples if the CoroutineCrudRepository.
* In the current two RestControllers, I've toyed with Channels and Flows with the Mongo Documents that are presented. 
* So how do you handle Channels and Flows with the RestController framework? 
* Let's say we have a queue of semi-trucks and an inspection station. They are waiting to get inspected (to see if their papers are okay, their vehicles have enough air in the tires, and whether they re carrying anything illegal, whatever...)
* So we have Trucks in a queue. More Trucks add to the queue as the day goes on. Trucks exit the queue as they pass through the inspection points. Various Inspectors view and report their findings. 
* On the Kotlin and technical side of things, we have a Flow of Trucks passing through several Channels of Inspection points. This process takes time. So a Truck passes from A to B to C to D over time. Maybe we should sequentially Post our findings (from A to D) and Get a final report.
* Or maybe we do a list of "asynch{}"-ed processes, givings ourselves a list of Deferred Reports that we can merge as our Inspectors complete their InspectionReport.
* ### Notes
* This is incomplete process currently, but will be completed as time goes on. Their are plenty of tests that are jump points which I used.
### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.2/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.2/gradle-plugin/reference/html/#build-image)
* [Coroutines section of the Spring Framework Documentation](https://docs.spring.io/spring/docs/6.1.3/spring-framework-reference/languages.html#coroutines)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#web.reactive)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/3.2.2/reference/htmlsingle/index.html#web.servlet.spring-mvc.template-engines)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

