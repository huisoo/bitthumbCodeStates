package example.ReactorPractice;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ReactorPracticeApplicationTests {

	/**
	 * ["Blenders", "Old", "Johnnie"] 와 "[Pride", "Monk", "Walker”] 를 순서대로 하나의 스트림으로 처리되는 로직 검증
	 */

	@Test
	public void concatWithDelay(){

		Flux<String> names1$ = Flux.just("Blenders", "Old", "Johnnie")
				.delayElements(Duration.ofSeconds(1));
		Flux<String> names2$ = Flux.just("Pride", "Monk", "Walker")
				.delayElements(Duration.ofSeconds(1));
		Flux<String> names$ = Flux.concat(names1$, names2$)
				.log();

		StepVerifier.create(names$)
				.expectSubscription()
				.expectNext("Blenders", "Old", "Johnnie", "Pride", "Monk", "Walker")
				.verifyComplete();
	}


	/**
	 * 1~100 까지의 자연수 중 짝수만 출력하는 로직 검증
	 */
	@Test
	public void evenNumber(){

		Flux<Integer> flux = Flux.range(1,100)
				.filter(num -> num%2==0)
				.log();

		flux.doOnNext(num->System.out.println(num)).subscribe();

		StepVerifier.create(flux)
				.thenConsumeWhile(num -> {
					assertThat(num%2==0);
					return true;
				})
				.verifyComplete();
	}

	/**
	 * “hello”, “there” 를 순차적으로 publish하여 순서대로 나오는지 검증
	 */
	@Test
	public void continuousSentence(){

		Flux<String> flux = Flux.just("hello", "there")
				.delayElements(Duration.ofSeconds(1))
				.publish()
				.log();


		StepVerifier.create(flux)
				.expectSubscription()
				.expectNext("hello", "there")
				.verifyComplete();
	}


	/**
	 * 아래와 같은 객체가 전달될 때 “JOHN”, “JACK” 등 이름이 대문자로 변환되어 출력되는 로직 검증
	 * Person("John", "[john@gmail.com](mailto:john@gmail.com)", "12345678")
	 * Person("Jack", "[jack@gmail.com](mailto:jack@gmail.com)", "12345678")
	 */
	@Test
	public void personLogic(){

		@Getter @Setter
		class Person {
			String name;
			String email;
			String address;

			public Person(String name, String email, String address){
				this.name = name;
				this.email = email;
				this.address = address;
			}

			public Person changeUpperName(){
				this.name=this.getName().toUpperCase();
				return this;
			}
		}

		Flux<Person> flux = Flux.just(new Person("John", "[john@gmail.com](mailto:john@gmail.com)", "12345678"), new Person("Jack", "[jack@gmail.com](mailto:jack@gmail.com)", "12345678"))
				.map(person -> {
					return person.changeUpperName();
				});

		flux.doOnNext(g->System.out.println(g.getName())).subscribe();

		StepVerifier.create(flux)
				.expectSubscription()
				.assertNext(p->assertThat(p.getName()).isEqualTo("JOHN"))
				.assertNext(p->assertThat(p.getName()).isEqualTo("JACK"))
				.verifyComplete();

	}



	/**
	 * ["Blenders", "Old", "Johnnie"] 와 "[Pride", "Monk", "Walker”]를 압축하여 스트림으로 처리 검증
	 * 예상되는 스트림 결과값 ["Blenders Pride", "Old Monk", "Johnnie Walker”]
	 */
	@Test
	public void strZipWith(){

		Flux<String> names1$ = Flux.just("Blenders", "Old", "Johnnie")
				.delayElements(Duration.ofSeconds(1));
		Flux<String> names2$ = Flux.just("Pride", "Monk", "Walker")
				.delayElements(Duration.ofSeconds(1));

		Flux<String> names$ = names1$.zipWith(names2$, (one, two) -> one + " " + two).log();

		StepVerifier.create(names$)
				.expectSubscription()
				.expectNext("Blenders Pride", "Old Monk", "Johnnie Walker")
				.verifyComplete();

	}


	/**
	 * ["google", "abc", "fb", "stackoverflow”] 의 문자열 중 5자 이상 되는 문자열만 대문자로 비동기로 치환하여 1번 반복하는 스트림으로 처리하는 로직 검증
	 * 예상되는 스트림 결과값 ["GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW"]
	 */
	@Test
	public void test6(){

		Flux<String> sentence = Flux.just("google", "abc", "fb", "stackoverflow")
				.filter(str -> str.length() >= 5)
				.flatMap(str -> Flux.just(str.toUpperCase()))
						.subscribeOn(Schedulers.boundedElastic())
						.repeat(1)
						.log();

		StepVerifier.create(sentence)
				.expectSubscription()
				.expectNext("GOOGLE", "STACKOVERFLOW", "GOOGLE", "STACKOVERFLOW")
				.verifyComplete();

	}

}
