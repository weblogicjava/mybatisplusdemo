## 背景
用多了公司的开发平台都快忘了最初mybatis的模样了，整好趁着最近要开一个非公司平台开发的项目，就拉出架势重新搞一搞mybatis的基本配置。
以下使用版本几乎都是截止2022/07/08最新的版本了：
> springboot：2.7.1
> mybatis-plus-boot-starter：3.5.2
> druid-spring-boot-starter：1.2.8，这个1.2.11总是提示我com.sun个几个包有问题，就降到1.2.8了；
> mybatis-plus-generator：3.5.2

## 创建数据库
```sql
create table if not exists city(
    id      int auto_increment primary key,
    name    varchar(30) null,
    state   varchar(30) null,
    country varchar(30) null
);
```
## Maven依赖pom.xml
```xml
<properties>
  <java.version>1.8</java.version>
  <maven.compiler.source>8</maven.compiler.source>
  <maven.compiler.target>8</maven.compiler.target>
</properties>
<dependencies>
  <!-- springboot项目依赖 -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
  <!-- mysql数据库驱动 -->
  <dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
  </dependency>
  <!-- Lombok -->
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>

  <!-- mybatisplus所需依赖及Druid数据库源 -->
  <dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
  </dependency>
  <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.2.8</version>
  </dependency>

  <!-- 代码生成所需依赖 -->
  <dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.2</version>
  </dependency>
  <dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
  </dependency>

</dependencies>
```
## 基本配置application.yaml
```yaml
server:
  port: 9090

spring:
  datasource:
    name: ds1
    druid:
      name: mysql
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://127.0.0.1:3306/mb
      username: root
      password: root
      max-active: 20
      initial-size: 1
      max-wait: 6000
      min-idle: 1
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 30000
      validation-query: select 'x'
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-open-prepared-statements: 20

logging:
  config: classpath:logback-boot.xml
```
## 日志文件配置logback-boot.xml
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %green([%thread]) %highlight(%-5level) %boldMagenta(%logger{15}) - %cyan(%msg%n)</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <appender name="syslog"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <File>log/app.log</File>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>log/app.%d.%i.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy  class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>20MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
        <encoder>
            <pattern>
                %d %p (%file:%line\)- %m%n
            </pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>
    <logger name="com.example.mybatis" level="DEBUG">
        <appender-ref ref="syslog" />
    </logger>
    <logger name="org.apache.spark" level="WARN">
        <appender-ref ref="STDOUT" />
    </logger>
</configuration>
```
## 代码生成
### 代码生成功能
```java
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/mb" ;
        String username = "root" ;
        String password = "root" ;
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("songwz") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.mybatis") // 设置父包名
                            .moduleName("system") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("city") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

}
```
### 代码生成结果
![image.png](https://cdn.nlark.com/yuque/0/2022/png/904796/1657267600129-0979665d-9e3f-416e-92c4-7585e6b22bfc.png#clientId=u05fa2200-86be-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=223&id=udc80e403&margin=%5Bobject%20Object%5D&name=image.png&originHeight=446&originWidth=420&originalType=binary&ratio=1&rotation=0&showTitle=false&size=39032&status=done&style=none&taskId=u030d3436-807e-4dd2-b128-5713aed9e67&title=&width=210)
### 完善controller
```java
@Autowired
private ICityService iCityService;

@RequestMapping(value = "/city/insert", method = RequestMethod.POST)
public void insertCity(@RequestParam(name = "action") String action) {
    City city = new City();
    city.setCountry("CN");
    city.setName("China");
    city.setState("SD");
    //插入新的数据
    //iCityService.save(city);

    //更新数据
    city.setId(21);
    UpdateWrapper<City> updateWrapper = new UpdateWrapper<>(city);
    iCityService.updateById(city);

    //数据分页处理
    Page<City> page = new Page<>(1,10);
    QueryWrapper<City> queryWrapper = new QueryWrapper<>();
    page = iCityService.page(page,queryWrapper);
    log.info(page.toString());
    log.info(page.getRecords().toString());
}
```


