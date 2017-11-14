/*
 * Copyright 201100 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package calc;

import calc.DTO.StatsDTO;
import calc.entity.Stats;
import calc.entity.Tournament;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static jdk.nashorn.internal.objects.NativeMath.random;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DTOTests {

    @Autowired
    private MockMvc mockMvc;

    public class StatsDtoUnitTest {

        private ModelMapper modelMapper = new ModelMapper();

        @Test
        public void whenConvertPostEntityToPostDto_thenCorrect() {
            Stats s = new Stats();
            s.setStatsId((new Double(Math.random())).longValue());
            s.setScore(random(100));
            s.setGameCount((int) Math.round(Math.random()*1000));
            s.setWinCount((int) Math.round(Math.random()*1000));
            s.setLoseCount((int) Math.round(Math.random()*1000));
            s.setTieCount((int) Math.round(Math.random()*1000));
            s.setWinStreak((int) Math.round(Math.random()*1000));
            s.setLoseStreak((int) Math.round(Math.random()*1000));
            s.setTieStreak((int) Math.round(Math.random()*1000));
            s.setBestScore(random(100));
            s.setWorstScore(random(100));
            s.setLonguestWinStreak((int) Math.round(Math.random()*1000));
            s.setLonguestLoseStreak((int) Math.round(Math.random()*1000));
            s.setTournament(new Tournament());

/*
            Sport sport = new Sport();
            sport.setSportId(secureRandomLong());
            sport.setName(UUID.randomUUID().toString());

            Tournament t = new Tournament();
            t.setTournamentId(secureRandomLong());
            t.setName(UUID.randomUUID().toString());
            t.setDisplayName(UUID.randomUUID().toString());
            t.setIsOver(Math.random() < 0.5);
            t.setSport(sport);

            User u = new User();
            u.setUserId(secureRandomLong());
            u.setEmail(UUID.randomUUID().toString());
            u.setFirst(UUID.randomUUID().toString());
            u.setLast(UUID.randomUUID().toString());
            u.setUsername(UUID.randomUUID().toString());
            u.setStats();
            u.setOutcomes();
            u.setPassword(UUID.randomUUID().toString());

            s.setTournament(t);*/



            StatsDTO statsDTO = modelMapper.map(s, StatsDTO.class);

            s.setStatsId((new Double(Math.random())).longValue());
            s.setScore(random(100));
            s.setGameCount((int) Math.round(Math.random()*1000));
            s.setWinCount((int) Math.round(Math.random()*1000));
            s.setLoseCount((int) Math.round(Math.random()*1000));
            s.setTieCount((int) Math.round(Math.random()*1000));
            s.setWinStreak((int) Math.round(Math.random()*1000));
            s.setLoseStreak((int) Math.round(Math.random()*1000));
            s.setTieStreak((int) Math.round(Math.random()*1000));
            s.setBestScore(random(100));
            s.setWorstScore(random(100));
            s.setLonguestWinStreak((int) Math.round(Math.random()*1000));
            s.setLonguestLoseStreak((int) Math.round(Math.random()*1000));
            s.setTournament(new Tournament());

            assert statsDTO.getStatsId() == s.getStatsId();
            assert statsDTO.getScore() == s.getScore();
            assert statsDTO.getBestScore() == s.getBestScore();
            assert statsDTO.getGameCount() == s.getGameCount();
            assert statsDTO.getWinCount() == s.getWinCount();
            assert statsDTO.getLoseCount() == s.getLoseCount();
            assert statsDTO.getTieCount() == s.getTieCount();
            assert statsDTO.getWinStreak() == s.getWinStreak();
            assert statsDTO.getLoseStreak() == s.getLoseStreak();
            assert statsDTO.getTieStreak() == s.getTieStreak();
            assert statsDTO.getLonguestWinStreak() == s.getLonguestWinStreak();
            assert statsDTO.getLonguestLoseStreak() == s.getLonguestLoseStreak();
            assert statsDTO.getLonguestTieStreak() == s.getLonguestTieStreak();
        }

        @Test
        public void whenConvertPostDtoToPostEntity_thenCorrect() {
            /*
            PostDto postDto = new PostDto();
            postDto.setId(Long.valueOf(1));
            postDto.setTitle(randomAlphabetic(100));
            postDto.setUrl("www.test.com");

            Post post = modelMapper.map(postDto, Post.class);
            assertEquals(postDto.getId(), post.getId());
            assertEquals(postDto.getTitle(), post.getTitle());
            assertEquals(postDto.getUrl(), post.getUrl());*/
        }
    }
}
