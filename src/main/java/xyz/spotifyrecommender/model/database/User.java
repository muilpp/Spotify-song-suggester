package xyz.spotifyrecommender.model.database;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "access_token")
    private String accessToken;

	@Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "access_revoked")
    private String accessRevoked;

    @Column(name = "avoid_spanish_music")
    private String avoidSpanishMusic;

    @Column(name = "short_term_tracks")
    private String shortTermTracks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

	public User() {
        //Hibernate needs a no-arg constructor to instantiate the object via reflection
    }

    public User(String userName, String accessToken, String refreshToken, String accessRevoked,
    		String avoidSpanishMusic, String shortTermTracks, LocalDateTime createdAt) {
        this.userName = userName;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessRevoked = accessRevoked;
        this.avoidSpanishMusic = avoidSpanishMusic;
        this.shortTermTracks = shortTermTracks;
        this.createdAt = createdAt;
    }

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessRevoked() {
        return accessRevoked;
    }

    public void setAccessRevoked(String accessRevoked) {
        this.accessRevoked = accessRevoked;
    }

    public String getAvoidSpanishMusic() {
		return avoidSpanishMusic;
	}

	public void setAvoidSpanishMusic(String avoidSpanishMusic) {
		this.avoidSpanishMusic = avoidSpanishMusic;
	}

    public String getShortTermTracks() {
		return shortTermTracks;
	}

	public void setShortTermTracks(String shortTermTracks) {
		this.shortTermTracks = shortTermTracks;
	}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}