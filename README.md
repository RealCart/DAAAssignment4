# Advanced Graph Algorithms Implementation Guide

---

## Table of Contents

- [Overview](#overview)
- [Algorithm Specifications](#algorithm-specifications)
- [Test Dataset Catalog](#test-dataset-catalog)
- [Complexity & Performance Analysis](#complexity--performance-analysis)
- [Implementation Guide](#implementation-guide)
- [Best Practices & Optimization](#best-practices--optimization)

---

## Overview

This implementation suite focuses on three fundamental directed graph algorithms, each optimized for specific use cases in dependency analysis, scheduling, and path computation. Our testing framework evaluates performance across 9 carefully designed test graphs ranging from 6 to 100 nodes.

<img width="715" height="392" alt="Снимок экрана 2025-11-02 в 22 47 11" src="https://github.com/user-attachments/assets/87212934-79ff-4f89-9d1f-438bf1724082" />

**Core Algorithms:**
- Strongly Connected Components (Tarjan's Algorithm)
- Topological Ordering (Kahn's BFS-based approach)
- Single-Source Shortest/Longest Paths in DAGs

---

## Algorithm Specifications

###  Strongly Connected Components (Tarjan)

Identifies maximal subgraphs where every vertex is reachable from every other vertex within the subgraph.

| Property | Value |
|----------|-------|
| **Time Complexity** | O(n + m) |
| **Space Complexity** | O(n) |
| **Approach** | Single-pass DFS with low-link tracking |
| **Primary Use Cases** | Cycle detection, graph condensation, dependency resolution |

**Key Metrics Captured:**
- DFS node visitation count
- Edge traversal statistics
- Component size distribution

**Output Components:**
- List of all SCCs
- Node-to-SCC mapping
- Condensation graph structure

---

### Topological Sort (Kahn)

Produces a linear vertex ordering respecting all edge directions (source → target).

| Property | Value |
|----------|-------|
| **Time Complexity** | O(n + m) |
| **Space Complexity** | O(n) |
| **Approach** | BFS-style with in-degree tracking |
| **Primary Use Cases** | Task scheduling, build systems, prerequisite ordering |

**Tracked Operations:**
- Queue insertion operations (`pushes`)
- Queue removal operations (`pops`)
- In-degree computation overhead

**Key Indicators:**
- Push/Pop ratio reveals parallelism potential
- Failed ordering indicates cycle presence

---

### DAG Path Algorithms

#### Shortest Path Computation

Efficiently computes minimum-weight paths from a single source vertex.

| Property | Value |
|----------|-------|
| **Time Complexity** | O(n + m) |
| **Space Complexity** | O(n) |
| **Approach** | Topological order + single-pass relaxation |
| **Advantage** | Faster than Dijkstra for DAGs |

**Metrics:**
- Successful edge relaxations
- Maximum reachable distance
- Path reconstruction

#### Longest Path (Critical Path Method)

Determines maximum-weight paths, essential for project scheduling.

| Property | Value |
|----------|-------|
| **Time Complexity** | O(n + m) |
| **Implementation** | Weight negation + shortest path algorithm |
| **Applications** | CPM, PERT, resource allocation |

---

## Test Dataset Catalog

### Graph Density Metrics


**Classification:**
- **Sparse:** < 0.10 (linear-like behavior)
- **Medium:** 0.10 - 0.30 (balanced structure)
- **Dense:** > 0.30 (quadratic tendencies)

---

### Small-Scale Graphs (6-8 nodes)

#### s1_dag
- **Nodes:** 6 | **Edges:** 6 | **Density:** 0.20
- **Structure:** Simple acyclic chain
- **Use Case:** Basic algorithm validation

#### s2_cycle
- **Nodes:** 6 | **Edges:** 7 | **Density:** 0.23
- **Structure:** Contains single cycle
- **Use Case:** SCC detection testing

#### s3_mixed
- **Nodes:** 8 | **Edges:** 10 | **Density:** 0.18
- **Structure:** Hybrid acyclic/cyclic
- **Use Case:** Edge case handling

---

### Medium-Scale Graphs (12-20 nodes)

#### m1_mix
- **Nodes:** 12 | **Edges:** 17 | **Density:** 0.13
- **Structure:** Multiple small cycles
- **Use Case:** Multi-component analysis

#### m2_dense
- **Nodes:** 15 | **Edges:** 45 | **Density:** 0.21
- **Structure:** Highly connected
- **Use Case:** Performance stress testing

#### m3_scc
- **Nodes:** 20 | **Edges:** 30 | **Density:** 0.08
- **Structure:** Several distinct SCCs
- **Use Case:** Condensation efficiency

---

### Large-Scale Graphs (25-100 nodes)

#### l1_sparse
- **Nodes:** 25 | **Edges:** 30 | **Density:** 0.05
- **Structure:** Long sequential chain
- **Use Case:** Deep recursion testing

#### l2_medium
- **Nodes:** 50 | **Edges:** 120 | **Density:** 0.05
- **Structure:** Moderate connectivity
- **Use Case:** Real-world simulation

#### l3_dense
- **Nodes:** 100 | **Edges:** 500 | **Density:** 0.05
- **Structure:** Complex mesh
- **Use Case:** Scalability evaluation

---

## Complexity & Performance Analysis

### Performance by Graph Topology

#### Chain Structures
- **SCC Count:** n (every node isolated)
- **Topo Sort:** Sequential, minimal queue size
- **Path Finding:** Every edge utilized
- **Bottleneck:** Deep recursion stack

#### Star/Hub Patterns
- **SCC Count:** n (no cycles)
- **Topo Sort:** High parallelism (many simultaneous zero-degree nodes)
- **Path Finding:** Few relaxations needed
- **Advantage:** Shallow depth, wide branching

#### Tree Hierarchies
- **SCC Count:** n (acyclic)
- **Topo Sort:** Multiple roots per level
- **Path Finding:** Branching exploration
- **Characteristic:** Balanced depth/width

#### Dense Meshes
- **SCC Count:** Low (large components)
- **Topo Sort:** Operates on condensed graph
- **Path Finding:** Many redundant edges
- **Challenge:** High edge-to-node ratio

---

### Empirical Results Summary

**Tarjan's SCC:**
- Always visits exactly n nodes
- Explores ≤ m edges (early termination possible)
- More cycles → fewer components → faster condensation
- Dense graphs incur higher edge-traversal overhead

**Kahn's Topological Sort:**
- Always processes exactly n nodes (pops = n)
- Push count ≥ n (equality only for strict chains)
- High push/pop ratio signals parallelism
- Linear structures have minimal queue overhead

**DAG Shortest Paths:**
- **Sparse graphs:** ~100% edge utilization
- **Medium graphs:** 50-80% edges contribute
- **Dense graphs:** <50% useful edges (many redundant)
- Relaxation percentage inversely correlates with density

---

### Density Impact on Runtime

| Density Range | Behavior | Algorithm Impact |
|---------------|----------|------------------|
| **< 0.10** | Near-linear | All algorithms perform optimally |
| **0.10 - 0.30** | Moderate | O(n+m) dominates, noticeable edge overhead |
| **> 0.30** | Quadratic tendency | SCC/Topo approach O(n²), SSSP maintains O(n+m) |

---

### SCC Condensation Benefits

**Concept:** Replace each SCC with a single supernode

**Performance Gain:**
- Original graph: n nodes → Condensed graph: n' nodes (number of SCCs)
- Speedup factor: approximately n/n'
- Best scenario: No cycles (n' = n) → no benefit
- Worst scenario: Single SCC (n' = 1) → trivial topology

**Example:** 100-node graph with 10 SCCs yields ~10× speedup for subsequent operations

---

## Implementation Guide

### System Requirements

```
Java: 11 or higher
Maven: 3.6+
```

### Quick Start

**Build Project:**
```bash
mvn clean compile
```

**Execute Full Test Suite:**
```bash
mvn exec:java -Dexec.mainClass="org.example.App"
```
*Generates performance metrics in `out/metrics.csv`*

**Run Specific Graph:**

1. Edit `src/main/java/org/example/App.java`
2. Modify: `String selectedFile = "your_graph.json";`
3. Execute:
```bash
mvn compile exec:java -Dexec.mainClass="org.example.App"
```

---

### Project Architecture

```
project-root/
│
├── data/                          
│   ├── s1_dag.json
│   ├── s2_cycle.json
│   ├── s3_mixed.json
│   ├── m1_mix.json
│   ├── m2_dense.json
│   ├── m3_scc.json
│   ├── l1_sparse.json
│   ├── l2_medium.json
│   └── l3_dense.json
│
├── src/main/java/
│   ├── graph/
│   │   ├── common/                 
│   │   │   ├── Graph.java
│   │   │   ├── JsonGraphReader.java
│   │   │   └── WeightedDAG.java
│   │   ├── scc/                   
│   │   │   ├── TarjanSCC.java
│   │   │   └── Condensation.java
│   │   ├── topo/                   
│   │   │   └── KahnTopo.java
│   │   └── dagsp/                 
│   │       ├── DagShortestPaths.java
│   │       └── DagLongestPath.java
│   └── org/example/
│       └── App.java                
│
├── src/test/java/graph/            
│   ├── TarjanSCCTest.java
│   ├── KahnTopoTest.java
│   ├── DagShortestPathsTest.java
│   └── DagLongestPathTest.java
│
└── out/
    └── metrics.csv                 
```

---

### Testing Framework

**Run All Tests:**
```bash
mvn test
```

**Individual Test Execution:**
```bash
mvn test -Dtest=TarjanSCCTest
mvn test -Dtest=KahnTopoTest
mvn test -Dtest=DagShortestPathsTest
mvn test -Dtest=DagLongestPathTest
```

---

### Expected Output Format

Example execution on `s1_dag.json`:

```
[s1_dag.json] SCC: count=6, sizes=[1,1,1,1,1,1]
[s1_dag.json] Topo: [5,4,3,2,1,0]
[s1_dag.json] SSSP: sourceComp=c5, farthest=c0, dist=7, path=[c5->c4->c2->c1->c0]
[s1_dag.json] Critical: length=9, path=[c5->c3->c2->c1->c0]
```

**Interpretation:**
- 6 SCCs detected (each node forms its own component)
- Valid topological ordering produced
- Shortest path: distance 7 from c5 to c0
- Longest path (critical): length 9 via alternate route

---

## Best Practices & Optimization

### Algorithm Selection Guide

#### When to Use Tarjan's SCC

 **Recommended for:**
- Cycle detection requirements
- Graph condensation preprocessing
- Dependency conflict resolution
- Deadlock analysis

 **Avoid when:**
- Graph is guaranteed acyclic
- Only simple reachability needed

 **Optimization Tips:**
- Implement iterative DFS for deep graphs (avoid stack overflow)
- Cache SCC results for repeated queries
- Consider parallelizing independent component analysis

---

#### When to Use Kahn's Topological Sort

 **Recommended for:**
- Task scheduling with dependencies
- Build system ordering
- Curriculum prerequisite planning
- Compilation order determination

 **Avoid when:**
- Graph contains cycles (combine with SCC first)
- Partial ordering is sufficient

 **Optimization Tips:**
- Check if result size < n to detect cycles early
- Monitor queue size for parallelism opportunities
- Use `ArrayDeque` for optimal queue performance
- Precompute in-degrees once if running multiple sorts

---

#### When to Use DAG Shortest Paths

 **Recommended for:**
- Minimum-cost path in acyclic networks
- Resource optimization problems
- Scenarios where Dijkstra is overkill

 **Avoid when:**
- Graph has cycles (use Bellman-Ford or Dijkstra)
- Need all-pairs distances (use Floyd-Warshall)

💡 **Optimization Tips:**
- Reuse topological ordering across multiple queries
- Early termination: stop after target node processed
- Process nodes in batches at same topological level for parallelism

---

#### When to Use DAG Longest Paths

 **Recommended for:**
- Critical Path Method (CPM) in project management
- PERT analysis
- Maximum reward pathfinding
- Resource scheduling optimization

 **Optimization Tips:**
- Ensure weights represent durations/costs accurately
- Negate weights and reuse shortest-path infrastructure
- Identify critical path nodes for priority resource allocation

---

### Performance Optimization Strategies

#### 1. Graph Condensation Pipeline

**Multi-stage approach for cyclic graphs:**

```
Original Graph (n nodes, m edges)
    ↓
[Run Tarjan's SCC]
    ↓
Condensation Graph (n' SCCs, m' edges)
    ↓
[Run Topological Sort]
    ↓
[Run Path Algorithms]
```

**Expected Speedup:** ~(n/n') × for subsequent operations

**When to Apply:**
- Graph contains cycles
- Running multiple queries
- n/n' ratio > 2

---

#### 2. Topological Order Caching

**Scenario:** Multiple shortest-path queries on same DAG

**Strategy:**
1. Compute topological order once: O(n + m)
2. Store result in memory
3. Reuse for each path query: saves O(n + m) per query

**Break-even Point:** 2+ queries on same graph

---

#### 3. Early Exit Optimization

**Applicable to:** Single-target shortest path queries

**Implementation:**
- Stop relaxation after target's predecessors processed
- Average speedup: ~50% for mid-graph targets
- Maximum speedup: ~90% for near-source targets

**Code Pattern:**
```java
for (int node : topoOrder) {
    relaxEdges(node);
    if (node == target && allPredecessorsProcessed(target)) {
        break; 
    }
}
```

---

#### 4. Memory Optimization

**Technique:** Primitive array usage

| Instead of | Use | Savings |
|------------|-----|---------|
| `ArrayList<Integer>` | `int[]` | ~60% memory |
| `LinkedList<Node>` | `ArrayDeque<Integer>` | ~40% memory + faster |
| `HashMap<Integer, Long>` | `long[]` indexed | ~70% memory + O(1) access |

**Impact:** Significant for large graphs (n > 10,000)

---

#### 5. Parallelization Opportunities

**Independent SCC Processing:**
```java
List<SCC> components = tarjan.findSCCs();
components.parallelStream()
    .forEach(scc -> processComponent(scc));
```

**Topological Sort Parallelism:**
- All zero in-degree nodes at current level are independent
- Process them concurrently in thread pool
- Synchronize only on in-degree updates

**Path Computation:**
- Nodes at same topological level can relax edges in parallel
- Requires thread-safe distance updates

---

### Scale-Specific Recommendations

#### Small Graphs (n < 100)
- **Focus:** Code clarity over optimization
- **Strategy:** Use straightforward implementations
- **Performance:** Sub-millisecond execution, optimization unnecessary

#### Medium Graphs (100 ≤ n ≤ 10,000)
- **Focus:** Algorithm choice matters
- **Strategy:**
    - Cache topological orders
    - Consider SCC condensation if cyclic
    - Monitor memory for dense graphs
- **Performance:** Millisecond to sub-second range

#### Large Graphs (n > 10,000)
- **Focus:** Optimization critical
- **Strategy:**
    - Always condense via SCC first (if applicable)
    - Use iterative implementations (avoid stack overflow)
    - Profile to identify bottlenecks
    - Consider parallel processing
    - Employ memory-efficient data structures
- **Performance:** Seconds to minutes depending on density

---

### Common Bottlenecks & Solutions

| Bottleneck | Symptom | Solution |
|------------|---------|----------|
| Deep recursion | Stack overflow in SCC | Implement iterative DFS |
| Dense graph edge traversal | O(n²) behavior | Condense via SCC first |
| Repeated topo computation | Redundant O(n+m) cost | Cache topological order |
| Memory pressure | OutOfMemoryError | Use primitive arrays, clear intermediate data |
| Sequential processing | Underutilized CPU | Parallelize independent components/levels |

