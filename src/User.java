import java.util.*;

public class User
        extends
        Person
{

    private SubscriptionType subscriptionType;

    private List<Movie> watchHistory;

    private Set<Genre> favoriteGenres;

    private double wallet;

    public User(
            int id,
            String name,
            int age,
            SubscriptionType subscriptionType,
            double wallet
    )
    {
        super(id, name, age);
        setSubscriptionType(subscriptionType);
        setWallet(wallet);
        this.watchHistory=new ArrayList<>();
        this.favoriteGenres=new HashSet<>();
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public double getWallet() {
        return wallet;
    }

    public List<Movie> getWatchHistory() {
        return new ArrayList<>(watchHistory);
    }

    public Set<Genre> getFavoriteGenres() {
        return new HashSet<>(favoriteGenres);
    }

    private void setSubscriptionType(SubscriptionType subscriptionType) {
        if(subscriptionType==null){
            throw new IllegalArgumentException("Subscription Type cannot be null");
        }
        this.subscriptionType = subscriptionType;
    }

    private void setWallet(double wallet) {
        if(wallet<0){
            throw new IllegalArgumentException("Wallet cannot be less than 0");
        }
        this.wallet = wallet;
    }

    public void changeSubscriptionType(SubscriptionType subscriptionType) {
        setSubscriptionType(subscriptionType);
    }

    public void watchMovie(Movie movie){
        if(movie==null){
            throw new IllegalArgumentException("Movie cannot be null");
        }
        if(!movie.isAvailable()){
            throw new IllegalArgumentException("Movie is not available");
        }
        watchHistory.add(movie);
    }

    public boolean addFavoriteGenre(Genre genre){
        if(genre==null){
            throw new IllegalArgumentException("Genre cannot be null");
        }
        return favoriteGenres.add(genre);
    }

    public boolean removeFavoriteGenre(Genre genre){
        if(genre==null){
            throw new IllegalArgumentException("Genre cannot be null");
        }
        return favoriteGenres.remove(genre);
    }

    public void deposit(double amount){
        if(amount<=0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        setWallet(wallet+amount);
    }

    public boolean withdraw(double amount){
        if(amount<=0){
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if(amount>wallet){
            return false;
        }
        setWallet(wallet-amount);
        return true;
    }

    public int getWatchCount(){
        return watchHistory.size();
    }

    public boolean hasWatched(Movie movie){
        if(movie==null){
            throw new IllegalArgumentException("Movie cannot be null");
        }
        return watchHistory.contains(movie);
    }

    public boolean hasFavoriteGenre(Genre genre){
        if(genre==null){
            throw new IllegalArgumentException("Genre cannot be null");
        }
        return favoriteGenres.contains(genre);
    }

    public boolean hasFavoriteGenres() {
        return !favoriteGenres.isEmpty();
    }

    public boolean hasWatchHistory() {
        return !watchHistory.isEmpty();
    }

    @Override
    public String toString() {
        return new StringBuilder(super.toString())
                .append("Subscription Type : ").append(subscriptionType).append("\n")
                .append("Wallet : ").append(wallet).append("\n")
                .append("Watched Movies Count : ").append(getWatchCount()).append("\n")
                .append("Favorite Genres : ").append(favoriteGenres).append("\n")
                .toString();
    }
}
