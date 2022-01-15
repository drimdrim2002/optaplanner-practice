************************************************************************
file with basedata            : md374_.bas
initial value random generator: 28475
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  22
horizon                       :  165
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     20      0       29       17       29
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          3           6  14  21
   3        3          3           5   7   9
   4        3          2          16  19
   5        3          3           8  12  21
   6        3          2           8  10
   7        3          3           8  10  16
   8        3          2          19  20
   9        3          2          10  16
  10        3          2          11  15
  11        3          1          13
  12        3          3          13  14  15
  13        3          1          17
  14        3          2          18  20
  15        3          1          17
  16        3          1          21
  17        3          2          18  20
  18        3          1          19
  19        3          1          22
  20        3          1          22
  21        3          1          22
  22        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     4       5    7    7    6
         2     7       2    7    7    4
         3    10       2    5    7    2
  3      1     4       8    3    6    7
         2     5       6    3    5    4
         3     8       4    3    4    3
  4      1     3       7   10    8    5
         2     3       5   10    8    6
         3    10       5   10    5    2
  5      1     4       7   10    9    6
         2     9       6    9    7    5
         3    10       2    6    7    2
  6      1     2       2    4    9   10
         2     3       2    4    7   10
         3     7       1    3    6    9
  7      1     4       6    9    9    5
         2     5       5    8    8    4
         3     6       4    8    6    3
  8      1     4       6    8    5    5
         2     6       3    4    3    2
         3     6       3    4    1    3
  9      1     4       9    7    9    5
         2     5       7    4    8    5
         3     8       5    4    3    2
 10      1     1       7    6    7    8
         2     2       6    5    7    8
         3     5       6    2    7    8
 11      1     8       4    7    8    8
         2     9       2    7    7    5
         3    10       2    6    6    2
 12      1     1       9    6    6   10
         2     5       9    5    4    9
         3     9       8    4    3    9
 13      1     5       4    8    4    9
         2     7       4    7    4    8
         3    10       4    5    3    8
 14      1     3       8    7    5    4
         2     6       6    7    5    4
         3     8       3    7    4    3
 15      1     3       7    9    4    9
         2     6       7    9    4    8
         3    10       6    8    4    4
 16      1     5       6    6    6    9
         2     7       6    4    4    9
         3    10       3    2    3    8
 17      1     1       4   10    2    4
         2     6       2    8    2    4
         3     7       2    7    1    1
 18      1     5       9    4   10    5
         2    10       9    3    7    3
         3    10       9    3    5    4
 19      1     1       2    9   10    8
         2     2       2    7    5    8
         3     5       1    6    5    8
 20      1     1       4   10    2    7
         2     6       4    9    2    7
         3     8       4    8    1    6
 21      1     3      10    6    6    9
         2     4       9    4    5    9
         3     8       9    2    2    7
 22      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   20   24  120  128
************************************************************************
