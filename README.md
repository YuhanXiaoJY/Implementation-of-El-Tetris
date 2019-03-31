## An Implementation of El-Tetris

**Yuhan Xiao**

------

### Description

El-Tetris improves Pierre Dellacherie’s Algorithm greatly to play  tetris. The key polish focuses on weights of the six features listed as follow:

- **Landing Height:** The height where the piece is put 
- **Rows eliminated:** e * b
  - e:  the number of rows eliminated
  - b:  the number of cells of the piece which make contributions to eliminating the rows
- **Row Transitions:** The total number of row transitions. A row transition occurs when an empty cell is adjacent to a filled cell on the same row and vice versa.
- **Column Transitions:** The total number of column transitions. A column transition occurs when an empty cell is adjacent to a filled cell on the same column and vice versa.
- **Number of Holes:** A hole is an empty cell that has at least one filled cell above it in the same column.
- **Well Sums:** A well is a succession of empty cells such that their left cells and right cells are both filled.



Here are the weights:

| Feature |       Weight        |
| :-----: | :-----------------: |
|    1    | -4.500158825082766  |
|    2    | 3.4181268101392694  |
|    3    | -3.2178882868487753 |
|    4    | -9.348695305445199  |
|    5    | -7.899265427351652  |
|    6    | -3.3855972247263626 |





------

#### Result

Given random kinds of piece, the AI can eliminate about 100,000 rows at most.