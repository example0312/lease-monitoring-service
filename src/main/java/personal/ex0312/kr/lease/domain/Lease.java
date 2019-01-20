package personal.ex0312.kr.lease.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lease {
    private boolean isMoreData;
    private List<Article> articleList;
}