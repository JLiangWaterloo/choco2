%-----------------------------------------------------------------------------%
% Requires exactly 'n' variables in 'x' to take the value 'v'.
%-----------------------------------------------------------------------------%

predicate exactly_int(int: n, array[int] of var int: x, int: v) =
    global_exactly(n,x,v);

%-----------------------------------------------------------------------------%

% The implementation of the constraint in the choco solver.
% This should not be called directly, instead the definition above should
% be used.
predicate global_exactly(int: n, array[int] of var int: x, int: v);

%-----------------------------------------------------------------------------%