In this sample, you'll build a native GraalVM image for running web workloads in AWS Lambda.



## Build the native image

Before starting the build, you must clone or download the code in **pet-store-native**.

1. Change into the project directory: `native`
2. Run the following to build a Docker container image which will include all the necessary dependencies to build the application 
   ```
   docker build -t al2023-graalvm21:native-web .
   ```
3. Build the application within the previously created build image
   ```
   docker run -it -v `pwd`:`pwd` -w `pwd` -v ~/.m2:/root/.m2 al2023-graalvm21:native-web ./mvnw clean -Pnative package -DskipTests
   ```



####  PowerShell
```
docker run -it -v "${pwd}:/project" -v "$env:USERPROFILE.m2:/root/.m2" -w /project al2023-graalvm21:native-web mvn clean -Pnative package -DskipTests

docker run -it -v "${pwd}:/project" -w /project al2023-graalvm21:native-web mvn clean -Pnative package -DskipTests

```


4. After the build finishes, you need to deploy the function:
 ```
   sam deploy --guided
   
   sam deploy --guided --profile sv
 ```

5. Test using order_example.json and order_updated.json

