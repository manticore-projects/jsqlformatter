-- SIMPLE
select  ACID
        , Count( * )
from (  select /*+ parallel driving_site(a) */
            A.ACID
            , A.TRAN_DATE_BAL
            , A.EOD_DATE
            , A.END_EOD_DATE
            , B.TRAN_DATE_BAL
            , B.EOD_DATE
            , B.END_EOD_DATE
        from CFE.TMP_EAB A
            inner join CFE.EXT_EAB_20210428 B
                on A.ACID = B.ACID
        where A.EOD_DATE <= '28-Apr-2021'
            AND A.END_EOD_DATE >= '28-Apr-2021'
            AND B.EOD_DATE <= '28-Apr-2021'
            AND B.END_EOD_DATE >= '28-Apr-2021'
            AND A.TRAN_DATE_BAL != B.TRAN_DATE_BAL )
group by ACID
having Count( * ) > 1
;