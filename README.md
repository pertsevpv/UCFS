# srcgll

* [About](https://github.com/cyb3r-b4stard/srcgll#about)
* [Usage](https://github.com/cyb3r-b4stard/srcgll#usage)
  * [From sources](https://github.com/cyb3r-b4stard/srcgll#from-sources)
  * [RSM Format Example](https://github.com/cyb3r-b4stard/srcgll#rsm-format-example)


## About
**srcgll** is a Huawei SRC internship project for implementing incremental error recovery parsing algorithm based on GLL in Kotlin

## Usage

### Command Line Interface

```text
Usage: srcgll options_list
Options: 
    --recovery [ON]    -> recovery mode { Value should be one of [on, off], default is on}
    --inputPath        -> Path to input txt file (always required) { String }
    --grammarPath      -> Path to RSM grammar txt file (always required) { String }
    --outputStringPath -> Path to output txt file (always required) { String }
    --outputSPPFPath   -> Path to output dot file (always required) { String }
    --help, -h         -> Usage info
```

### From sources

#### Step 1. Clone repository

`git clone https://github.com/cyb3r-b4stard/srcgll.git`

or 

`git clone git@github.com:cyb3r-b4stard/srcgll.git`

or 

`gh repo clone cyb3r-b4stard/srcgll`

#### Step 2. Go to the folder

`cd srcgll`

#### Step 3. Run the help command

`gradle run --args="--help"`

You will see the ["Options list"](https://github.com/cyb3r-b4stard/srcgll#command-line-interface) message.

#### Step 4. Run the dot command

`dot -Tpdf result_sppf.dot > result_sppf.pdf`

#### Example

```text
run --args="--inputPath input.txt --grammarPath src/test/resources/cli/TestRSMReadWriteTXT/simple_golang.txt 
--outputStringPath output.txt --outputSPPFPath result_sppf.dot"
```

### RSM Format Example

```text
StartState(id=0,nonterminal=Nonterminal("S"),isStart=true,isFinal=false)
State(id=0,nonterminal=Nonterminal("S"),isStart=true,isFinal=false)
State(id=1,nonterminal=Nonterminal("S"),isStart=false,isFinal=false)
State(id=4,nonterminal=Nonterminal("S"),isStart=false,isFinal=false)
State(id=3,nonterminal=Nonterminal("S"),isStart=false,isFinal=true)
State(id=2,nonterminal=Nonterminal("S"),isStart=false,isFinal=false)
State(id=6,nonterminal=Nonterminal("S"),isStart=false,isFinal=true)
State(id=5,nonterminal=Nonterminal("S"),isStart=false,isFinal=false)
TerminalEdge(tail=0,head=1,terminal=Terminal("subClassOf_r"))
TerminalEdge(tail=0,head=4,terminal=Terminal("type_r"))
TerminalEdge(tail=1,head=3,terminal=Terminal("subClassOf"))
NonterminalEdge(tail=1,head=2,nonterminal=Nonterminal("S"))
TerminalEdge(tail=4,head=6,terminal=Terminal("type"))
NonterminalEdge(tail=4,head=5,nonterminal=Nonterminal("S"))
TerminalEdge(tail=2,head=3,terminal=Terminal("subClassOf"))
TerminalEdge(tail=5,head=6,terminal=Terminal("type"))
```