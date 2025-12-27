import java.util.*;

abstract class Node {
    int graphId; // the ID used in the adjacency list
    String name; // name of center or victim
    String location; // common attribute

    Node(int graphId, String name, String location) {
        this.graphId = graphId;
        this.name = name;
        this.location = location;
    }

    public int getGraphId() {
        return graphId;
    }

    // public abstract String getType(); // each subclass will define this
}

class Center extends Node {
    int ID;
    HashMap<String, Double> resources;
    Center left;
    Center right;

    public Center(int graphID, String location, int ID) {
        super(graphID, "Center-" + ID, location);
        this.ID = ID;
        resources = new HashMap<>();
        this.left = null;
        this.right = null;
    }
}

class ReliefCenters {
    Center root;
    Scanner sc = new Scanner(System.in);
    DisasterReliefNetwork network;

    public ReliefCenters(DisasterReliefNetwork network) {
        root = null;
        this.network = network;
    }

    public void addReliefCenterInfo() {
        System.out.println("Enter the ID of the Relief Center: ");
        int ID = sc.nextInt();
        sc.nextLine();
        if (ID < 0) {
            System.out.println("ID cannot be negative!");
            System.out.println("Please enter a valid ID!");
            ID = sc.nextInt();
            sc.nextLine();
        }
        System.out.println("Enter the location of the Relief Center: ");
        String location = sc.nextLine();

        int graphID = network.generateGlobalID();
        Center newRC = new Center(graphID, location, ID);
        System.out.println("Enter resources for this relief center (at least 3).");
        System.out.println("Please press Enter without typing anything to stop adding resources.");

        int count = 0;
        while (true) {
            System.out.print("Enter resource name (food, water, etc.): ");
            String category = sc.nextLine();

            // If user presses Enter without typing anything
            if (category.isEmpty()) {
                if (count < 3) {
                    System.out.println("A relief center must provide at least 3 resources!");
                    continue; // don’t break yet — force them to enter more
                } else {
                    break; // user can exit once 3+ entries exist
                }
            }

            System.out.print("Enter quantity (in tonnes): ");
            double quantity = sc.nextDouble();
            sc.nextLine();
            if (quantity < 0.0) {
                System.out.println("Quantity of resources cannot be negative!");
                System.out.println("Please enter a valid value!");
                continue;
            }

            newRC.resources.put(category, quantity);
            count++;

            System.out.println("Added " + category + " (" + quantity + " units)");
        }

        System.out.println("\nFinal Resources for this Relief Center:");
        for (Map.Entry<String, Double> e : newRC.resources.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue() + " tonnes");
        }

        if (newRC.resources.size() < 3) {
            System.out.println("Not enough resources — this center cannot be categorized as a relief center.");
        } else {
            System.out.println("This center qualifies as a Relief Center!");
            root = addReliefCenter(newRC, root);
            network.registerNode(newRC);
        }

    }

    public Center findCenterByID(Center root, int ID) {
        if (root == null)
            return null;
        if (root.graphId == ID)
            return root;
        if (root.graphId > ID)
            return findCenterByID(root.left, ID);
        else
            return findCenterByID(root.right, ID);
    }

    private Center addReliefCenter(Center newNode, Center root) {
        if (root == null) {
            root = newNode;
            return root;
        }
        if (root.ID > newNode.ID) {
            root.left = addReliefCenter(newNode, root.left);
        } else {
            root.right = addReliefCenter(newNode, root.right);
        }
        return root;
    }

    public void displayReliefCenters(Center root) {
        if (root == null) {
            return;
        }
        displayReliefCenters(root.left);
        System.out.println("ID: " + root.ID);
        System.out.println("Location: " + root.location);
        System.out.println("Resources currently available: ");
        for (Map.Entry<String, Double> entry : root.resources.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue() + " tonnes");
        }
        displayReliefCenters(root.right);
    }

    public void auditResources(Center root) {
        if (root == null) {
            return;
        }

        // Left subtree
        auditResources(root.left);

        // Current node
        if (root.resources.size() < 3) {
            System.out.println(" Relief Center ID: " + root.ID + " (" + root.location + ")");
            System.out.println("Has only " + root.resources.size() + " resources.");
            System.out.print("Do you want to add more resources (A) or delete this center (D)? ");
            String choice = sc.nextLine().trim().toUpperCase();

            if (choice.equals("A")) {
                addResources(root); // helper function
            } else if (choice.equals("D")) {
                System.out.println("Deleting Relief Center " + root.ID + "...");
                root = deleteReliefCenter(root, root.ID);
            } else {
                System.out.println("Invalid choice. Skipping this center.");
            }
        }
        // Right subtree
        auditResources(root.right);
    }

    public void replenishResources(Center center) {
        System.out.println("Please press Enter without typing anything to stop adding resources.");
        while (true) {
            System.out.print("Enter resource name (food, water, etc.): ");
            String category = sc.nextLine();

            // If user presses Enter without typing anything
            if (category.isEmpty()) {
                break;
            }

            System.out.print("Enter quantity (in tonnes): ");
            double quantity = sc.nextDouble();
            sc.nextLine();

            if (quantity < 0.0) {
                System.out.println("Quantity of resources cannot be negative!");
                System.out.println("Please enter a valid value!");
                continue;
            }

            center.resources.put(category, quantity);

            System.out.println("Added " + category + " (" + quantity + " units)");
        }
        System.out.println("Resources have been replenished!");
    }

    public void addResources(Center center) {
        System.out.println("Adding resources to " + center.location + ":");

        while (center.resources.size() < 3) {
            System.out.print("Enter resource name (or press Enter to stop): ");
            String category = sc.nextLine();

            if (category.isEmpty())
                break;

            System.out.print("Enter quantity (in tonnes): ");
            double qty = sc.nextDouble();
            sc.nextLine();
            if (qty <= 0.0) {
                System.out.println("Quantity of resources cannot be negative!");
                System.out.println("Please enter a valid value!");
                continue;
            }

            center.resources.put(category, qty);
            System.out.println("Added " + category + " (" + qty + " units)");
        }

        if (center.resources.size() >= 3)
            System.out.println("Center now qualifies as a Relief Center!");
        else
            System.out.println("Still below minimum resource requirement.");
    }

    private Center inorderSuccessor(Center root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    public Center deleteReliefCenter(Center root, int ID) {
        if (root == null) {
            System.out.println("There are no such relief centers to delete!");
            return null;
        }
        if (root.ID > ID) {
            root.left = deleteReliefCenter(root.left, ID);
        } else if (root.ID < ID) {
            root.right = deleteReliefCenter(root.right, ID);
        } else {
            if (root.left == null && root.right == null) {
                return null;
            }
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            Center IS = inorderSuccessor(root.right);
            root.ID = IS.ID;
            root.location = IS.location;
            root.resources = IS.resources;
            root.right = deleteReliefCenter(root.right, IS.ID);
        }
        System.out.println("The relief center has been successfully removed!");
        return root;
    }

    // added rn ----------------------------------------------------------
    public void dispatchHighestPriorityRequest(VictimRequests vr) {
        Victim v = vr.dequeue();
        if (v == null)
            return;

        Center bestCenter = findNearestEligibleCenter(v);
        if (bestCenter == null) {
            System.out.println("No relief center can satisfy this request.");
            return;
        }

        System.out.println("Dispatching from Center " + bestCenter.ID + " to Victim " + v.ID);
        for (Map.Entry<String, Double> e : v.request.entrySet()) {
            bestCenter.resources.put(e.getKey(), bestCenter.resources.get(e.getKey()) - e.getValue());
        }
        System.out.println("Updated resources at Center " + bestCenter.ID + ": " + bestCenter.resources);
    }

    private Center findNearestEligibleCenter(Victim v) {
        return findNearestEligibleCenterRec(root, v, null, Double.POSITIVE_INFINITY);
    }

    private Center findNearestEligibleCenterRec(Center node, Victim v, Center best, double bestDist) {
        if (node == null)
            return best;

        if (hasEnoughResources(node, v)) {
            double dist = network.dijkstraEarlyExit(node.graphId, v.graphId);
            if (dist < bestDist) {
                bestDist = dist;
                best = node;
            }
        }

        best = findNearestEligibleCenterRec(node.left, v, best, bestDist);
        best = findNearestEligibleCenterRec(node.right, v, best, bestDist);
        return best;
    }

    private boolean hasEnoughResources(Center c, Victim v) {
        for (Map.Entry<String, Double> req : v.request.entrySet()) {
            if (!c.resources.containsKey(req.getKey()) || c.resources.get(req.getKey()) < req.getValue()) {
                return false;
            }
        }
        return true;
    }

}

class Victim extends Node {
    int ID;
    int priority;
    HashMap<String, Double> request;

    public Victim(int graphID, int ID, int priority, String location) {
        super(graphID, "Victim-" + ID, location);
        this.ID = ID;
        this.priority = priority;
        request = new HashMap<>();
    }
}

class VictimRequests {
    private ArrayList<Victim> queue;
    Scanner sc = new Scanner(System.in);
    DisasterReliefNetwork network;

    public VictimRequests(DisasterReliefNetwork network) {
        queue = new ArrayList<>();
        this.network = network;
    }

    private void enqueue(Victim req) {

        int i = 0;
        // Find correct position (higher priority first)
        while (i < queue.size() && queue.get(i).priority >= req.priority) {
            i++;
        }
        queue.add(i, req); // insert at position i
    }

    public void addVictimRequest() {
        System.out.println("Enter request ID: ");
        int ID = sc.nextInt();
        sc.nextLine();

        if (ID < 0) {
            System.out.println("ID cannot be negative!");
            System.out.println("Please enter a valid ID!");
            ID = sc.nextInt();
            sc.nextLine();
        }
        System.out.println("Enter the location of the request: ");
        String location = sc.nextLine();
        System.out.println("Enter the severity of the request on a scale of 1 to 5 (1= less severe and 5= critcal)");
        int priority = sc.nextInt();
        sc.nextLine();

        if (priority < 1 || priority > 5) {
            System.out.println(
                    "Please enter the severity of the request on a scale of 1 to 5 (1= less severe and 5= critcal)");
            priority = sc.nextInt();
            sc.nextLine();
        }

        int graphID = network.generateGlobalID();
        Victim newRQ = new Victim(graphID, ID, priority, location);
        System.out.println("Enter resources needed for this request:");
        System.out.println("Please press Enter without typing anything to stop adding resources.");

        while (true) {
            System.out.print("Enter resource name (food, water, etc.): ");
            String category = sc.nextLine();

            // If user presses Enter without typing anything
            if (category.isEmpty()) {
                break;
            }

            System.out.print("Enter quantity (in tonnes): ");
            double quantity = sc.nextDouble();
            sc.nextLine();

            if (quantity < 0.0) {
                System.out.println("Quantity of resources cannot be negative!");
                System.out.println("Please enter a valid value!");
                continue;
            }

            newRQ.request.put(category, quantity);

            System.out.println("Request for " + quantity + " tonnes of " + category + " has been logged!)");
        }
        enqueue(newRQ);
        network.registerNode(newRQ);

    }

    public Victim dequeue() {
        if (queue.isEmpty()) {
            System.out.println("No victim requests left to process.");
            return null;
        }
        return queue.remove(0);
    }

    // Peek (see next to serve)
    public Victim peek() {
        if (queue.isEmpty())
            return null;
        return queue.get(0);
    }

    void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        System.out.println("Pending Victim Requests (High priority -> Low priority):");
        for (int i = 0; i < queue.size(); i++) {
            System.out.println("Name: " + queue.get(i).ID + ", Location: " + queue.get(i).location + ", Priority: "
                    + queue.get(i).priority);
            System.out.println("ID " + queue.get(i).ID + " requests: ");

            for (Map.Entry<String, Double> e : queue.get(i).request.entrySet()) {
                System.out.println(e.getKey() + " -> " + e.getValue() + " tonnes");
            }
        }
    }
}

class Edge {
    int vertex;
    double distance;

    public Edge(int vertex, double distance) {
        this.vertex = vertex;
        this.distance = distance;
    }
}

class DisasterReliefNetwork {
    private int nextGlobalID = 1;
    private HashMap<Integer, Node> nodeRegister;
    private HashMap<Integer, ArrayList<Edge>> adjacencyList;
    Scanner sc = new Scanner(System.in);

    public DisasterReliefNetwork() {
        nodeRegister = new HashMap<>();
        adjacencyList = new HashMap<>();
    }

    public int generateGlobalID() {
        return nextGlobalID++;
    }

    public void bfsTraversal() {
        if (adjacencyList.isEmpty()) {
            System.out.println("No nodes in the network yet!");
            return;
        }

        System.out.print("Enter starting node graphID for BFS: ");
        int startId = sc.nextInt();

        if (!adjacencyList.containsKey(startId)) {
            System.out.println("Invalid graphID. No such node exists in the network.");
            return;
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.add(startId);
        visited.add(startId);

        System.out.println("\n=== BFS Traversal starting from Node " + startId + " ===");

        while (!queue.isEmpty()) {
            int current = queue.poll();
            Node currentNode = nodeRegister.get(current);
            System.out.println("Visited: " + currentNode.name + " (" + currentNode.graphId + "), Location: "
                    + currentNode.location);

            for (Edge e : adjacencyList.getOrDefault(current, new ArrayList<>())) {
                if (!visited.contains(e.vertex)) {
                    visited.add(e.vertex);
                    queue.add(e.vertex);
                }
            }
        }

        System.out.println("=== End of BFS ===\n");
    }

    public void registerNode(Node n) {
        nodeRegister.put(n.graphId, n);
        System.out.println("Location registered: " + n.name + " (Location ID: " + n.graphId + ")");
        System.out.println("Enter number of neighboring locations connected to this node:");
        int count = sc.nextInt();

        for (int i = 0; i < count; i++) {
            System.out.println("Enter neighbor node ID: ");
            int neighborId = sc.nextInt();

            System.out.println("Enter distance (in km): ");
            double distance = sc.nextDouble();

            adjacencyList.putIfAbsent(n.graphId, new ArrayList<>());
            adjacencyList.putIfAbsent(neighborId, new ArrayList<>());

            // add edge in both directions
            adjacencyList.get(n.graphId).add(new Edge(neighborId, distance));
            adjacencyList.get(neighborId).add(new Edge(n.graphId, distance));
        }
    }

    // added rn ----------------------------------------------------
    // public double dijkstraEarlyExit(int sourceId, int targetId) {
    // if (!adjacencyList.containsKey(sourceId) ||
    // !adjacencyList.containsKey(targetId))
    // return Double.POSITIVE_INFINITY;

    // PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a ->
    // a[1]));
    // HashMap<Integer, Double> dist = new HashMap<>();

    // for (int node : adjacencyList.keySet())
    // dist.put(node, Double.POSITIVE_INFINITY);
    // dist.put(sourceId, 0.0);
    // pq.add(new int[] { sourceId, 0 });

    // while (!pq.isEmpty()) {
    // int[] curr = pq.poll();
    // int node = curr[0];
    // double d = curr[1];
    // if (node == targetId)
    // return d;

    // for (Edge e : adjacencyList.getOrDefault(node, new ArrayList<>())) {
    // double newDist = d + e.distance;
    // if (newDist < dist.get(e.vertex)) {
    // dist.put(e.vertex, newDist);
    // pq.add(new int[] { e.vertex, (int) newDist });
    // }
    // }
    // }
    // return Double.POSITIVE_INFINITY;
    // }
    public double dijkstraEarlyExit(int sourceId, int targetId) {
        if (!adjacencyList.containsKey(sourceId) || !adjacencyList.containsKey(targetId))
            return Double.POSITIVE_INFINITY;

        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        HashMap<Integer, Double> dist = new HashMap<>();

        for (int node : adjacencyList.keySet())
            dist.put(node, Double.POSITIVE_INFINITY);
        dist.put(sourceId, 0.0);
        pq.add(new double[] { sourceId, 0.0 });

        while (!pq.isEmpty()) {
            double[] curr = pq.poll();
            int node = (int) curr[0];
            double d = curr[1];
            if (node == targetId)
                return d;

            for (Edge e : adjacencyList.getOrDefault(node, new ArrayList<>())) {
                double newDist = d + e.distance;
                if (newDist < dist.get(e.vertex)) {
                    dist.put(e.vertex, newDist);
                    pq.add(new double[] { e.vertex, newDist });
                }
            }
        }
        return Double.POSITIVE_INFINITY;
    }

}

public class ReliefCentersCode {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DisasterReliefNetwork network = new DisasterReliefNetwork();
        ReliefCenters reliefCenters = new ReliefCenters(network);
        VictimRequests victimRequests = new VictimRequests(network);

        int choice;
        do {
            System.out.println("\n==================== Disaster Relief Management System ====================");
            System.out.println("1. Add Relief Center");
            System.out.println("2. Display All Relief Centers");
            System.out.println("3. Audit Relief Centers (Add/Delete Resources)");
            System.out.println("4. Add resources to a relief center");
            System.out.println("5. Add Victim Request");
            System.out.println("6. Display All Victim Requests");
            System.out.println("7. Dispatch Highest Priority Request");
            System.out.println("8. BFS of graph");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    reliefCenters.addReliefCenterInfo();
                    break;

                case 2:
                    reliefCenters.displayReliefCenters(reliefCenters.root);
                    break;

                case 3:
                    reliefCenters.auditResources(reliefCenters.root);
                    break;

                case 4:
                    System.out.print("Enter Relief Center ID to add/replenish resources: ");
                    int idToAdd = sc.nextInt();
                    sc.nextLine();

                    Center target = reliefCenters.findCenterByID(reliefCenters.root, idToAdd);
                    if (target != null) {
                        reliefCenters.replenishResources(target);
                    } else {
                        System.out.println("No relief center found with ID " + idToAdd);
                    }
                    break;

                case 5:
                    victimRequests.addVictimRequest();
                    break;

                case 6:
                    victimRequests.displayQueue();
                    break;

                case 7:
                    reliefCenters.dispatchHighestPriorityRequest(victimRequests);
                    break;

                case 8:
                    network.bfsTraversal();
                    break;

                case 9:
                    System.out.println("Exiting the program. Stay safe!");
                    break;

                default:
                    System.out.println("Invalid choice! Please enter a valid option.");
                    break;
            }
        } while (choice != 9);

        sc.close();
    }
}