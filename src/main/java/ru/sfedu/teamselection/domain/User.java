package ru.sfedu.teamselection.domain;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "azure_oid", unique = true)
    private String azureId;

    @Column
    private String fio;

    @Column
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Student student;

    @Column(name = "is_enabled", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isEnabled = Boolean.FALSE;

    @Column(name = "is_locked", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean isLocked = Boolean.FALSE;

    @Column(name = "is_remind_enabled", nullable = false)
    @ColumnDefault("true")
    @Builder.Default
    private Boolean isRemindEnabled = Boolean.TRUE;

    //TODO
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "users_roles",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
//    private List<Role> roles;

    @Column(name = "created_at", nullable = false)
    @ColumnDefault("'2024-08-04 10:23:54'::timestamp without time zone")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @ColumnDefault("'2024-08-04 10:23:54'::timestamp without time zone")
    @UpdateTimestamp
    private LocalDateTime  updatedAt;

    /**
     * Get the OAuth 2.0 token attributes
     *
     * @return the OAuth 2.0 token attributes
     */
    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "email", getEmail(),
                "name", getName()
        );
    }

    //@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(getRole());
    }

    //@Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //@Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    //@Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //@Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User that = (User) o;
        return fio.equals(that.fio)
                && email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fio, email);
    }

    @Override
    public String getName() {
        return fio;
    }
}
