package no.infoskjermen;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
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
