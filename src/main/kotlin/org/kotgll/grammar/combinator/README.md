## Declaration:
for nonterminals

`val S by NT()`

for terminals 

`val A = Term("a")`
## Operation:

### Concatenation
(.): Σ∗ × Σ∗ → Σ∗

a . b = a * b

### Kleene Star
$a* = U_{i=0}^{\inf}a^i$

a* = Many(a)

`todo a += Some(a)`

### Alternative 
a | b = a or b

### Production
A → B = A = B

### Optional 
a? -> a | Epsilon

`todo a? = opt(a)`
