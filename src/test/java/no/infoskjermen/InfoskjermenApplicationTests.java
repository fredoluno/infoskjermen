package no.infoskjermen;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
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
