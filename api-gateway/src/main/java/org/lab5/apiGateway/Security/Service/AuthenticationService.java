package org.lab5.apiGateway.Security.Service;

import lombok.RequiredArgsConstructor;
import org.lab5.apiGateway.Security.Model.JwtAuthenticationResponse;
import org.lab5.apiGateway.Security.Model.UserDetailsImpl;
import org.lab5.apiGateway.Security.Model.SignInRequest;
import org.lab5.apiGateway.Security.Model.SignUpRequest;
import org.lab5.common.Model.Entities.Owner;
import org.lab5.common.Model.Entities.Role;
import org.lab5.common.Repository.OwnerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final OwnerRepository ownerRepository;

    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest) {
        Owner owner = new Owner();
        owner.setUsername(signUpRequest.getUsername());
        owner.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        owner.setRole(Role.ROLE_USER);

        ownerRepository.save(owner);
        var userDetails = new UserDetailsImpl(owner);

        var jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signInRequest.getUsername(),
                            signInRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid username or password");
        }

        var userDetails = userService.loadUserByUsername(signInRequest.getUsername());

        var jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}
