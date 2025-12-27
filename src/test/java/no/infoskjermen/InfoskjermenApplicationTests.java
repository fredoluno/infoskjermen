package no.infoskjermen;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class InfoskjermenApplicationTests {

    @Autowired
	private InfoskjermenApplication infoskjerm;

	@Test
	public void testHello()  {

		System.out.println("ferdig");

		assertThat(this.infoskjerm.hello())
				.isEqualTo("hei");
	}

	@Test
	public void contextLoads() {
	}



}
