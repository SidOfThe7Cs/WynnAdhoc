package sidly.wynnadhoc;

import org.jspecify.annotations.NonNull;

import java.util.*;

public class Jugs {
    public static void main(String[] args) {
        List<List<Action>> solutions = Solver.findShortestSolutions();

        System.out.println("Found " + solutions.size() + " solution(s) with " +
                (solutions.isEmpty() ? 0 : solutions.get(0).size()) + " steps:");

        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("\nSolution " + (i + 1) + ":");
            Puzzle p = new Puzzle();
            System.out.println("Start: " + p.getState());
            for (Action action : solutions.get(i)) {
                p.applyAction(action);
                System.out.println("  " + action + " -> " + p.getState());
            }
        }
    }

    public static class Puzzle {
        private final Tube A;
        private final Tube B;
        private final Tube C;
        private String tracker = "";

        public Puzzle() {
            A = new Tube(9);
            B = new Tube(7);
            C = new Tube(5);
        }

        public Puzzle(Puzzle other) {
            this.A = new Tube(other.A);
            this.B = new Tube(other.B);
            this.C = new Tube(other.C);
            this.tracker = other.tracker;
        }

        public boolean isComplete() {
            return A.contains == 9 && B.contains == 6 && C.contains == 3;
        }

        public List<Action> getPossibleActions() {
            List<Action> actions = new ArrayList<>();

            // Fill actions
            if (!A.isFull()) actions.add(new Action(Type.FILL, TubeId.A, null));
            if (!B.isFull()) actions.add(new Action(Type.FILL, TubeId.B, null));
            if (!C.isFull()) actions.add(new Action(Type.FILL, TubeId.C, null));

            // Empty actions
            if (!A.isEmpty()) actions.add(new Action(Type.EMPTY, TubeId.A, null));
            if (!B.isEmpty()) actions.add(new Action(Type.EMPTY, TubeId.B, null));
            if (!C.isEmpty()) actions.add(new Action(Type.EMPTY, TubeId.C, null));

            // Move actions
            // A -> B, A -> C
            if (!A.isEmpty() && !B.isFull()) actions.add(new Action(Type.MOVE, TubeId.A, TubeId.B));
            if (!A.isEmpty() && !C.isFull()) actions.add(new Action(Type.MOVE, TubeId.A, TubeId.C));
            // B -> A, B -> C
            if (!B.isEmpty() && !A.isFull()) actions.add(new Action(Type.MOVE, TubeId.B, TubeId.A));
            if (!B.isEmpty() && !C.isFull()) actions.add(new Action(Type.MOVE, TubeId.B, TubeId.C));
            // C -> A, C -> B
            if (!C.isEmpty() && !A.isFull()) actions.add(new Action(Type.MOVE, TubeId.C, TubeId.A));
            if (!C.isEmpty() && !B.isFull()) actions.add(new Action(Type.MOVE, TubeId.C, TubeId.B));

            return actions;
        }

        public String getState() {
            return A.contains + "," + B.contains + "," + C.contains;
        }

        public void applyAction(Action action) {
            Tube from, to = null;

            from = switch (action.from) {
                case A -> A;
                case B -> B;
                case C -> C;
            };
            if (action.to != null) {
                to = switch (action.to) {
                    case A -> A;
                    case B -> B;
                    case C -> C;
                };
            }

            String actionStr = switch (action.type) {
                case FILL -> {
                    from.fill();
                    yield "fill(" + action.from + ")";
                }
                case EMPTY -> {
                    from.empty();
                    yield "empty(" + action.from + ")";
                }
                case MOVE -> {
                    from.move(to);
                    yield "move(" + action.from + " -> " + action.to + ")";
                }
            };
            tracker += actionStr + " ";
            //System.out.println(actionStr + " from now contains: " + from.contains + ((to == null) ? "" : " to now contains: " + to.contains));
        }
    }

    public static class Tube {
        private final int size;
        private int contains = 0;

        public Tube(int size) {
            this.size = size;
        }

        public Tube(Tube tube) {
            this.size = tube.size;
            this.contains = tube.contains;
        }

        public void fill() {
            contains = size;
        }

        public void empty() {
            contains = 0;
        }

        public void move(Tube t) {
            contains = Math.max(0, (contains - t.add(contains)));
        }

        public int add(int x) {
            int after = Math.min(contains + x, size);
            int added = after - contains;
            contains = after;
            return added;
        }

        public boolean isFull() {
            return contains == size;
        }

        public boolean isEmpty() {
            return contains == 0;
        }
    }

    public enum TubeId {A, B, C}

    public enum Type {FILL, EMPTY, MOVE}

    public record Action(Type type, TubeId from, TubeId to) {
        @Override
        public @NonNull String toString() {
            if (type == Type.MOVE) {
                return "move(" + from + " -> " + to + ")";
            } else {
                return type.name().toLowerCase() + "(" + from + ")";
            }
        }
    }

    public static class Solver {
        public static List<List<Action>> findShortestSolutions() {
            Puzzle initial = new Puzzle();
            String targetState = "9,6,3";

            // BFS queue
            Queue<Node> queue = new LinkedList<>();
            Map<String, Integer> visited = new HashMap<>();

            queue.offer(new Node(initial, new ArrayList<>(), 0));
            visited.put(initial.getState(), 0);

            List<List<Action>> solutions = new ArrayList<>();
            int shortestSteps = Integer.MAX_VALUE;

            while (!queue.isEmpty()) {
                Node current = queue.poll();

                // If we found a solution and current depth exceeds shortest, stop
                if (current.depth > shortestSteps) {
                    break;
                }

                // Check if goal reached
                if (current.puzzle.getState().equals(targetState)) {
                    if (current.depth < shortestSteps) {
                        shortestSteps = current.depth;
                        solutions.clear();
                    }
                    solutions.add(new ArrayList<>(current.actions));
                    continue;
                }

                // Try all possible actions
                for (Action action : current.puzzle.getPossibleActions()) {
                    Puzzle next = new Puzzle(current.puzzle);
                    next.applyAction(action);

                    String nextState = next.getState();
                    int nextDepth = current.depth + 1;

                    // Only visit if not visited or found with same/shorter depth
                    if (!visited.containsKey(nextState) || visited.get(nextState) >= nextDepth) {
                        visited.put(nextState, nextDepth);
                        List<Action> nextActions = new ArrayList<>(current.actions);
                        nextActions.add(action);
                        queue.offer(new Node(next, nextActions, nextDepth));
                    }
                }
            }

            return solutions;
        }

        private record Node(Puzzle puzzle, List<Action> actions, int depth) {
        }
    }
}
