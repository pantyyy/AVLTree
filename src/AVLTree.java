import java.util.ArrayList;

//定义了两个泛型
//一个泛型K要具有可比较的属性
public class AVLTree<K extends Comparable<K> , V> {

    //内部类 , 树中的节点
    private class Node{
        public K key;
        public V value;
        public Node left , right;   //左右子树
        public int height;  //以当前节点为根 , 树的高度

        public Node(K key , V value){
            this.key = key;
            this.value = value;
            left = null;
            right = null;
            height = 1; //初始化时 , 当前节点的高度设置为1
        }
    }

    //内部属性 , 一棵树维护的根节点
    private Node root;
    private int size;

    //构造方法
    public AVLTree(){
        root = null;
        size = 0;
    }

    public int getSize(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }


    //判断该二叉树是否是一棵二分搜索树
    public boolean isBST(){

        //二分搜索树的性质 : 中序遍历是升序排序
        ArrayList<K> keys = new ArrayList<>();
        inOrder(root , keys);
        //因为是两两比较 , 所以从下标1开始即可
        for(int i = 1 ; i < keys.size() ; i++){
            if(keys.get(i - 1).compareTo(keys.get(i)) > 0)
                //因为是升序 , 所以不可能出现前一个元素大于后一个元素的情况
                //如果出现了 , 就表示不是一个二分搜索树
                return false;
        }
        return true;
    }

    //中序遍历
    private void inOrder(Node node , ArrayList<K> keys){
        if(node == null){
            return;
        }

        //对当前节点的左子树进行中序遍历
        inOrder(node.left , keys);
        //当前节点的左子树中序遍历结束 , 可以访问节点
        keys.add(node.key);
        //在对当前节点的右子树进行中序遍历
        inOrder(node.right , keys);

        //中序遍历结束

    }

    public boolean isBalanced(){
        return isBalanced(root);
    }

    //判断以Node为根的二叉树是否是一棵平衡二叉树
    private boolean isBalanced(Node node){
        if(node == null){
            return true;
        }

        int balanceFactor = getBalanceFactor(node);
        if(Math.abs(balanceFactor) > 1){
            return false;
        }

        //平衡的条件是每个节点都是平衡的
        return isBalanced(node.left) && isBalanced(node.right);
    }



    //获取节点node的高度
    private int getHeight(Node node){
        if(node == null)
            return 0;
        return node.height;
    }

    //获取节点node的平衡因子
    private int getBalanceFactor(Node node){
        if(node == null)
            return 0;

        //平衡因子 = |左子树高度 - 右子树高度|
        return getHeight(node.left) - getHeight(node.right);
    }


    // 对节点y进行向右旋转操作，返回旋转后新的根节点x
    //        y                              x
    //       / \                           /   \
    //      x   T4     向右旋转 (y)        z     y
    //     / \       - - - - - - - ->    / \   / \
    //    z   T3                       T1  T2 T3 T4
    //   / \
    // T1   T2
    //当前节点进行右旋
    private Node rightRotate(Node y){
        //创建指向x的指针和指向T3的指针
        Node x = y.left;
        Node T3 = x.right;

        //右旋
        y.left = T3;
        x.right = y;

        //更新height
        //注意x的高度是依赖于y的高度 , 所以在更新高度的时候需要先对y进行更新
        //然后再更新x
        y.height = Math.max(getHeight(y.left) , getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left) , getHeight(x.right)) + 1;

        //返回旋转之后的根节点
        return x;
    }

    // 对节点y进行向左旋转操作，返回旋转后新的根节点x
    //    y                             x
    //  /  \                          /   \
    // T1   x      向左旋转 (y)       y     z
    //     / \   - - - - - - - ->   / \   / \
    //   T2  z                     T1 T2 T3 T4
    //      / \
    //     T3 T4
    private Node leftRotate(Node y){
        Node x = y.right;
        Node T2 = x.left;

        //左旋
        y.right = T2;
        x.left = y;

        //更新height
        y.height = Math.max(getHeight(y.left) , getHeight(y.right)) + 1;
        x.height = Math.max(getHeight(x.left) , getHeight(x.right)) + 1;

        //返回旋转之后的根节点
        return x;
    }



    //向二分搜索树中添加新的元素(key , value)
    public void add(K key , V  value){
        root = add(root , key , value);
    }

    //向以node为根的二分搜索树中插入元素(key , value) , 递归算法
    //返回插入新节点之后 , 新树的根节点
    private Node add(Node node , K key , V value){
        //递归结束的情况
        if(node == null){
            size++;
            return new Node(key , value);
        }

        //判断应该往那颗子树中进行添加
        if(key.compareTo(node.key) < 0)
            //添加的节点 , 小于当前节点 , 向左子树中添加
            node.left = add(node.left , key , value);
        else if(key.compareTo(node.key) > 0)
            node.right = add(node.right , key , value);
        else
            node.value = value;

        //因为在左右子树中添加了节点 , 所以高度发生了变化
        //求出当前节点左右子树中比较高的那个 , 再加上1
        node.height = 1 + Math.max(getHeight(node.left)  , getHeight(node.right));

        //计算平衡因子
        int balanceFactor = getBalanceFactor(node);
        if(Math.abs(balanceFactor) > 1)
            //System.out.println("unbalanced :" + balanceFactor);

        //判断是否需要进行平衡的维护

        //LL的情况 , node的左子树的左子树的height应该 大于等于node的左子树的右子树的height
        //即getBalanceFactor(node.left) >= 0
        if(balanceFactor > 1 && getBalanceFactor(node.left) > 0){
            //balanceFactor > 1表示左子树比较重
            //getBalanceFactor(node.left) >= 0表示需要进行右旋
            node = rightRotate(node);

            return node;
        }

        //RR的情况 , node的右子树的右子树的height应该大于等于node的右子树的左子树的height
        //即getBalanceFactor(node.right) <= 0
        if(balanceFactor < -1 && getBalanceFactor(node.right) < 0){
            node = leftRotate(node);
            return node;
        }

        //LR的情况
        //node的左子树的左子树的height应该小于node的左子树的右子树的height
        if(balanceFactor > 1 && getBalanceFactor(node.left) < 0){
            //先对node.left进行左旋 , 这样原本是R比较重就转移到了L这边来
            //就由LR转换为了LL情况
            node.left = leftRotate(node.left);
            //LL情况 , 进行一次右旋即可
            node = rightRotate(node);
            return node;
        }

        //RL的情况
        if(balanceFactor < -1 && getBalanceFactor(node.right) > 0){
            node.right = rightRotate(node.right);
            node = leftRotate(node);
            return node;
        }


        return node;
    }


    //返回以node为根节点 , key所在的节点
    private Node getNode(Node node , K key){
        if(node == null){
            return null;
        }

        if(key.equals(node.key))
            return node;
        else if(key.compareTo(node.key) < 0){
            return getNode(node.left , key);
        }else{
            return getNode(node.right , key);
        }


    }


    //判断二分搜索树中是否包含key节点
    public boolean contains(K key){
        return getNode(root , key) != null;
    }


    //根据key获取value
    public V get(K key){
        Node node = getNode(root , key);
        return node == null ? null : node.value;
    }


    public void set(K key , V newValue){
        Node node = getNode(root , key);
        if(node == null)
            throw new IllegalArgumentException(key + "doesn't exist!");

        node.value = newValue;
    }


    //返回以node为根节点 , 树中最小值所在的节点
    //最小节点 , 即这颗树最左边的节点
    private Node minimum(Node node){
        if(node.left == null)
            return node;
        return minimum(node.left);
    }

    //删除掉以node为根的树中的最小节点
    //返回删除之后新树的根
    private Node removeMin(Node node){
        if(node.left == null){
            //当前node为需要删除的节点
            Node rightNode = node.right;    //保留指向当前节点右子树的指针
            node.right = null;
            size--;
            //返回删除最小节点之后 , 新树的根节点
            return rightNode;
        }

        node.left = removeMin(node.left);
        return node;
    }


    //从树中删除键为key的节点
    public V remove(K key){
        Node node = getNode(root , key);
        if(node != null){
            root = remove(root , key);
            return node.value;
        }

        return null;
    }

    //以node为根节点 , 删除key值的node
    //返回新树的根节点
    private Node remove(Node node , K key){
        if(node == null)
            return null;


        //因为删除之后 , 可能需要进行平衡的维护 , 所以不能立马返回node节点
        //因此使用一个指针来保存需要返回的节点
        Node retNode;
        //判断向哪个子树中进行删除操作
        if(key.compareTo(node.key) < 0){
            node.left = remove(node.left , key);
            //return node;
            retNode = node;
        }
        else if(key.compareTo(node.key) > 0){
            node.right = remove(node.right , key);
            //return node;
            retNode = node;
        }else{
            //当前节点是需要删除的节点
            //有三种情况
            //1.待删除节点左子树为空的情况
            if(node.left == null){
                //保留新树的根节点
                Node rightNode = node.right;
                node.right = null;
                size--;
                //return rightNode;
                retNode = rightNode;
            }

            //2.待删除节点右子树为空的情况
            else if(node.right == null){
                Node leftNode = node.left;
                node.left = null;
                size--;
                //return leftNode;
                retNode = leftNode;
            }

            else{
                //3.待删除节点左右子树均不为空的情况
                //找到比待删除节点大的最小节点 , 即待删除节点右子树的最小节点
                //用这个节点代替删除节点的位置

                //1.找到代替节点
                Node successor = minimum(node.right);
                //2.删除代替节点 , 并把删除节点的右子树指向删除之后的新树
                successor.right = remove(node.right , successor.key);
                //3.node的左子树接到代替节点的左子树上
                successor.left = node.left;

                //4.node删除和左右子树的联系
                node.left = node.right = null;

                //返回新的树根
                //return successor;
                retNode = successor;
            }

        }


        //因为删除了节点 , retNode有可能出现为null的情况 , 需要进行健壮性判断
        if(retNode == null){
            return null;
        }

        //更新节点的height
        retNode.height = 1 + Math.max(getHeight(retNode.left) , getHeight(retNode.right));

        //进行平衡的维护
        //判断4种情况 , 进行旋转

        // 计算平衡因子
        int balanceFactor = getBalanceFactor(retNode);

        // LL
        if (balanceFactor > 1 && getBalanceFactor(retNode.left) >= 0)
            return rightRotate(retNode);

        // RR
        if (balanceFactor < -1 && getBalanceFactor(retNode.right) <= 0)
            return leftRotate(retNode);

        // LR
        if (balanceFactor > 1 && getBalanceFactor(retNode.left) < 0) {
            retNode.left = leftRotate(retNode.left);
            return rightRotate(retNode);
        }

        // RL
        if (balanceFactor < -1 && getBalanceFactor(retNode.right) > 0) {
            retNode.right = rightRotate(retNode.right);
            return leftRotate(retNode);
        }

        return retNode;

    }




    public static void main(String[] args){

        System.out.println("Pride and Prejudice");

        ArrayList<String> words = new ArrayList<>();
        if(FileOperation.readFile("pride-and-prejudice.txt", words)) {
            System.out.println("Total words: " + words.size());

            AVLTree<String, Integer> map = new AVLTree<>();
            for (String word : words) {
                if (map.contains(word))
                    map.set(word, map.get(word) + 1);
                else
                    map.add(word, 1);
            }

            System.out.println("Total different words: " + map.getSize());
            System.out.println("Frequency of PRIDE: " + map.get("pride"));
            System.out.println("Frequency of PREJUDICE: " + map.get("prejudice"));

            System.out.println("is BST : " + map.isBST());
            System.out.println("is Balanced : " + map.isBalanced());

            for(String word : words){
                map.remove(word);
                if(!map.isBST() || !map.isBalanced()){
                    throw new RuntimeException();
                }
            }
        }

        System.out.println();
    }
}
