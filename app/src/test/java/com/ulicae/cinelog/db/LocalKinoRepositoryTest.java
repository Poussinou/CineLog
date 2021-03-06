package com.ulicae.cinelog.db;

import com.ulicae.cinelog.dao.DaoSession;
import com.ulicae.cinelog.dao.LocalKino;
import com.ulicae.cinelog.dao.LocalKinoDao;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * CineLog Copyright 2018 Pierre Rognon
 *
 *
 * This file is part of CineLog.
 * CineLog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CineLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CineLog. If not, see <https://www.gnu.org/licenses/>.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalKinoRepositoryTest {

    @Mock
    private DaoSession daoSession;

    @Mock
    private LocalKinoDao localKinoDao;

    @Mock
    private LocalKino localKino;

    @Before
    public void setUp() throws Exception {
        doReturn(localKinoDao).when(daoSession).getLocalKinoDao();
    }

    @Test
    public void findAllByYearAsc() throws Exception {
        QueryBuilder queryBuilder = mock(QueryBuilder.class);
        doReturn(queryBuilder).when(localKinoDao).queryBuilder();
        doReturn(queryBuilder).when(queryBuilder).orderAsc(LocalKinoDao.Properties.Rating);

        Query query = mock(Query.class);
        doReturn(query).when(queryBuilder).build();

        doReturn(Arrays.asList(localKino)).when(query).list();

        assertEquals(
                Arrays.asList(localKino),
                new LocalKinoRepository(daoSession).findAllByRating(true)
        );
    }

    @Test
    public void findAllByYearDesc() throws Exception {
        QueryBuilder queryBuilder = mock(QueryBuilder.class);
        doReturn(queryBuilder).when(localKinoDao).queryBuilder();
        doReturn(queryBuilder).when(queryBuilder).orderDesc(LocalKinoDao.Properties.Rating);

        Query query = mock(Query.class);
        doReturn(query).when(queryBuilder).build();

        doReturn(Arrays.asList(localKino)).when(query).list();

        assertEquals(
                Arrays.asList(localKino),
                new LocalKinoRepository(daoSession).findAllByRating(false)
        );
    }

    @Test
    public void findAll() throws Exception {
        List<LocalKino> kinoList = new ArrayList<LocalKino>() {{
            add(localKino);
        }};
        doReturn(kinoList).when(localKinoDao).loadAll();

        assertEquals(
                kinoList,
                new LocalKinoRepository(daoSession).findAll()
        );
    }

    @Test
    public void find() throws Exception {
        doReturn(localKino).when(localKinoDao).load(4L);

        assertEquals(
                localKino,
                new LocalKinoRepository(daoSession).find(4L)
        );
    }

    @Test
    public void findByMovieId() throws Exception {
        QueryBuilder queryBuilder = mock(QueryBuilder.class);
        doReturn(queryBuilder).when(localKinoDao).queryBuilder();
        doReturn(queryBuilder).when(queryBuilder).where(any(WhereCondition.class));//LocalKinoDao.Properties.Tmdb_id.eq(45));
        doReturn(queryBuilder).when(queryBuilder).limit(1);

        Query query = mock(Query.class);
        doReturn(query).when(queryBuilder).build();

        doReturn(Arrays.asList(localKino)).when(query).list();

        assertEquals(
                localKino,
                new LocalKinoRepository(daoSession).findByMovieId(45)
        );

    }

    @Test
    public void createOrUpdate() throws Exception {
        new LocalKinoRepository(daoSession).createOrUpdate(localKino);

        verify(localKinoDao).insertOrReplace(localKino);
    }

    @Test
    public void delete() throws Exception {
        // TODO delete the TmdbKino if necessary too.
        new LocalKinoRepository(daoSession).delete(5L);

        verify(localKinoDao).deleteByKey(5L);
    }
}