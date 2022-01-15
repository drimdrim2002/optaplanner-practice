************************************************************************
file with basedata            : mf16_.bas
initial value random generator: 1490112568
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  32
horizon                       :  242
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     30      0       19        1       19
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          3           5   6   8
   3        3          2           7  12
   4        3          3          19  25  29
   5        3          3          10  13  25
   6        3          2          16  17
   7        3          3           9  14  16
   8        3          1          24
   9        3          1          21
  10        3          3          11  12  22
  11        3          3          28  30  31
  12        3          2          21  27
  13        3          3          16  22  26
  14        3          2          15  31
  15        3          3          19  20  21
  16        3          1          18
  17        3          2          20  26
  18        3          2          20  24
  19        3          2          23  27
  20        3          1          23
  21        3          2          23  28
  22        3          1          24
  23        3          1          30
  24        3          1          28
  25        3          2          26  31
  26        3          1          27
  27        3          1          30
  28        3          1          29
  29        3          1          32
  30        3          1          32
  31        3          1          32
  32        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     5      10    5    0    6
         2     6      10    5    4    0
         3     8      10    3    1    0
  3      1     1       3    7    0    7
         2     4       3    6    7    0
         3     9       3    5    7    0
  4      1     1       3    3    7    0
         2     3       2    3    0    2
         3     4       2    1    5    0
  5      1     1       3    6    8    0
         2     2       3    6    6    0
         3     6       3    4    6    0
  6      1     2       6    9    8    0
         2     6       3    7    0    7
         3    10       1    6    0    3
  7      1     2       9    5    8    0
         2     4       9    4    6    0
         3    10       9    1    0   10
  8      1     3       5    4    7    0
         2     4       4    3    4    0
         3     5       4    3    0    3
  9      1     1       7    4   10    0
         2     7       5    4    0   10
         3     7       6    4    0    9
 10      1     2      10    6    8    0
         2     3       9    4    7    0
         3     9       9    2    0    5
 11      1     6      10    9    4    0
         2     7       9    9    4    0
         3     9       9    8    0    4
 12      1     3       9    9    7    0
         2     4       7    7    5    0
         3     9       3    6    4    0
 13      1     2       4    6    0    9
         2     8       3    6    0    7
         3    10       2    5    8    0
 14      1     1       5    8    8    0
         2     4       5    8    7    0
         3    10       5    5    5    0
 15      1     4       5    7    0    4
         2     6       5    6    9    0
         3     7       4    2    4    0
 16      1     3       9    3    7    0
         2     3       9    3    0    6
         3     6       5    2    0    6
 17      1     4       8   10    0    1
         2     9       8    6    0    1
         3    10       8    2    5    0
 18      1     2       3    4    0    7
         2     7       3    3    0    3
         3    10       3    3    3    0
 19      1     5       4    7    0    6
         2     6       4    5    1    0
         3    10       3    5    0    5
 20      1     1       7    9    0    8
         2     2       6    6    0    8
         3     8       6    3    0    7
 21      1     2       8    6    4    0
         2     2       7    6    6    0
         3     9       5    3    3    0
 22      1     2       6    8    0    8
         2     7       5    8    9    0
         3     9       4    7    0    6
 23      1     4      10    7    9    0
         2     6       9    6    0    7
         3     7       9    5    5    0
 24      1     1       4    8    4    0
         2     3       4    4    0    9
         3    10       3    2    3    0
 25      1     4       4    6    8    0
         2     6       4    6    0    5
         3     9       1    5    0    2
 26      1     2       3   10    0    3
         2     9       2    9    7    0
         3     9       3   10    5    0
 27      1     3       6    9    0    8
         2     3       6    9    7    0
         3     9       6    7    5    0
 28      1     3       6    7    0    8
         2     7       5    5    0    7
         3     8       5    1    0    7
 29      1     2       6    9    0    7
         2     4       5    9    0    5
         3     4       5    8    9    0
 30      1     1      10    8    2    0
         2     2       9    5    0    9
         3     3       9    2    0    9
 31      1     4       9   10    8    0
         2     5       7    7    0    7
         3     8       7    5    0    1
 32      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   50   52  103   90
************************************************************************
