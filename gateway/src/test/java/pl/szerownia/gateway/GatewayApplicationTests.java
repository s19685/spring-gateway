package pl.szerownia.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import static java.lang.Math.random;

//@SpringBootTest
class GatewayApplicationTests {

    @Test
    void contextLoads() {
        System.out.print(random()*0.01+52.1719743+" ");

        System.out.println(random()*0.01+20.80298568508463);

//        Flux
//                .range(0,50)
//                .map(i->Math.random()*0.01)
//                .subscribe(System.out::println);
    }

}
