package io.pivotal.demo.springboot.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.demo.springboot.models.User;
import io.pivotal.demo.springboot.repositories.UserRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository userRepository;

	/**
	 * GET /users -> get all users.
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Page<User> getUser(Pageable pageable) {

		return userRepository.findAll(pageable);
	}

	// /**
	// * GET /users/:id -> get a specific user.
	// */
	// @RequestMapping(value = "/users/{id}",
	// method = RequestMethod.GET,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// ResponseEntity<User> getUser(@PathVariable Long id) {
	// System.out.println("User by ID: " + id);
	// System.err.println("User by ID: " + id);
	// User user = userRepository.findOne(id);
	// if (user == null) {
	// return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	// }
	// return new ResponseEntity<>(user, HttpStatus.OK);
	// }

	/**
	 * GET /users/:email -> get a specific user by login id.
	 */
	@RequestMapping(value = "/users/{email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<User> getUser(@PathVariable String email) {
		final Iterable<User> users = userRepository.findAll();
		if (users != null) {
			for (final User user : users) {
				if (user.getEmail().equals(email)) {
					return new ResponseEntity<>(user, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * GET /users/count -> get a specific user by login id.
	 */
	@RequestMapping(value = "/users/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public int count() {
		int count = 0;
		final Iterable<User> users = userRepository.findAll();
		if (users != null) {
			final Iterator<User> it = users.iterator();
			while (it.hasNext()) {
				it.next();
				++count;
			}
		}
		return count;
	}

	/**
	 * POST /users -> Create a new user.
	 */
	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> create(@RequestBody User user) throws URISyntaxException {
		if (user.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new user cannot already have an ID").body(null);
		}

		User newUser = new User();
		newUser.setFirstname(user.getFirstname());
		newUser.setLastname(user.getLastname());
		newUser.setEmail(user.getEmail());
		userRepository.save(newUser);

		return ResponseEntity.created(new URI("/users/" + user.getId())).body(newUser);
	}

	/**
	 * PUT /users -> Updates an existing user.
	 */
	@RequestMapping(value = "/users", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<User> update(@RequestBody User user) throws URISyntaxException {
		userRepository.save(user);

		return ResponseEntity.ok().body(user);
	}

	/**
	 * DELETE /users/:id -> delete the "id" user.
	 */
	@RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> delete(@PathVariable Long id) {

		try {
			userRepository.delete(id);
		} catch (Exception e) {
		}

		return ResponseEntity.ok().build();
	}
}
