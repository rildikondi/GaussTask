# Clean Architecture

Clean Architecture with multimodules written in Java and the web layer is build with the help of Spring.

<img src="https://blog.cleancoder.com/uncle-bob/images/2012-08-13-the-clean-architecture/CleanArchitecture.jpg" alt="Clean Architecture img">



  
Clean Architecture main directory contains all the modules which realize the working of application.

<b> webapplication, inmemorydb and applicationdi</b> modules are part of the <b>Framework and drivers layer</b>.

<b>interfaceadpters</b> module is part of <b>Interface Adapters layer</b>.

<b>usecases </b> module is part of <b>Application Business Rules layer</b>.

<b>entities</b> module is part of <b>Enterprise Business Rules layer</b>.

<img src="https://github.com/rildikondi/CleanArchitectureSpring/blob/clean_architecture_by_layer/images/main_module_schema.jpg" >


<b>applicationdi</b> module uses <b>inmemorydb, usecases, interfaceadpters</b> modules to inject the dependencies that are needed in this application.

<img src="https://github.com/rildikondi/CleanArchitectureSpring/blob/clean_architecture_by_layer/images/applicationdi_schema.jpg" >

<b>Webapplication</b> module is part of <b>framework and drivers layer</b>.Framework and drivers layer is the outermost layer.
webapplication module depends on layers below it such as interface adapters and usecases.
webapplication module uses <b>spring framework</b>. The endpoints which are in this layer with help of spring annotations build together
the entrypoints for the input.
Example schema of one case of Gauss elimination for linear equations with Clean Architecture.

<img src="https://github.com/rildikondi/CleanArchitectureSpring/blob/clean_architecture_by_layer/images/webapplication_schema.jpg" >

## References:
[https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

[https://github.com/grant-burgess/clean-architecture-example-java-spring-boot)
