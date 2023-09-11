public class PrimeFinder implements java.util.Iterator<Integer> {

    int SIEVE_SIZE = 1 << 8;
    boolean[] isPrime;
    java.util.ArrayList<Integer> primes;
    int currentIndex;
    int sieveSize;

    public PrimeFinder(int sieveSize) {
        this.sieveSize = sieveSize;
        this.isPrime = new boolean[sieveSize];
        this.primes = new java.util.ArrayList<>();
        this.currentIndex = -1;

        java.util.Arrays.fill(isPrime, true);
    }

    public PrimeFinder() {
        this.sieveSize = SIEVE_SIZE;
        this.isPrime = new boolean[sieveSize];
        this.primes = new java.util.ArrayList<>();
        this.currentIndex = -1;

        java.util.Arrays.fill(isPrime, true);
    }

    @Override
    public Integer next() {
        if (currentIndex == -1) {
            findPrimes();
        }
        return primes.get(currentIndex++);
    }

    public boolean hasNext() {
        if (currentIndex == -1) {
            findPrimes();
        }
        return currentIndex < primes.size() && primes.get(currentIndex) != null;
    }

    private void findPrimes() {
        for (int i = 2; i< Math.sqrt(sieveSize); i++) {
            if(isPrime[i]) {
                for(int j = (i*i); j < sieveSize; j = j+i) {
                    isPrime[j] = false;
                }
            }
        }

        for (int i = 2; i< isPrime.length; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }
        currentIndex = 0;
    }

    public static void main(String[] args) {
        int sieveSize = 1 << 8;
        if (args.length > 0) {
            sieveSize = Integer.parseInt(args[0]);
        }
        PrimeFinder finder = new PrimeFinder(sieveSize);
        while (finder.hasNext()) {
            System.out.print(finder.next() + " ");
        }
        System.out.println("");
    }
}
