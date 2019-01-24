package personal.ex0312.kr.lease.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import personal.ex0312.kr.lease.api.NaverNewLandApiClient;
import personal.ex0312.kr.lease.domain.Article;
import personal.ex0312.kr.lease.domain.Lease;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeaseMonitorServiceTest {
    @InjectMocks
    private LeaseMonitorService leaseMonitorService;
    @Mock
    private NaverNewLandApiClient naverNewLandApiClient;
    @Mock
    private ArticleHandler articleHandler;
    @Mock
    private ArticlePolishService articlePolishService;

    @Test
    public void testCollectLeasesEveryOneMinute_whenGettingArticles_thenTheyShouldBePolished() {
        // given
        List<Article> firstArticleList = Arrays.asList(
            Article.builder().id("firstArticle").price("1억5,000").build()
        );

        List<Article> secondArticleList = Arrays.asList(
            Article.builder().id("secondArticle-1").price("1억2,000").build(),
            Article.builder().id("secondArticle-2").price("7,000").build()
        );

        Lease firstLease = Lease.builder()
            .isMoreData(true)
            .articleList(firstArticleList)
            .build();
        Lease secondLease = Lease.builder()
            .isMoreData(false)
            .articleList(secondArticleList)
            .build();

        when(naverNewLandApiClient.findLeases(anyLong(), anyString(), anyString(), anyString(), anyInt(), anyInt(), anyInt()))
            .thenReturn(firstLease)
            .thenReturn(secondLease);

        when(articlePolishService.polishArticles(anyList()))
            .thenReturn(Stream.concat(firstArticleList.stream(), secondArticleList.stream()).collect(Collectors.toList()));

        ArgumentCaptor<List<Article>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        // when
        leaseMonitorService.collectLeasesEveryOneMinute();

        // then
        verify(naverNewLandApiClient, times(2)).findLeases(anyLong(), anyString(), anyString(), anyString(), anyInt(), anyInt(), anyInt());
        verify(articlePolishService, times(1)).polishArticles(anyList());
        verify(articleHandler, times(1)).processArticles(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isNotNull();
        assertThat(argumentCaptor.getValue().size()).isEqualTo(3);
    }
}