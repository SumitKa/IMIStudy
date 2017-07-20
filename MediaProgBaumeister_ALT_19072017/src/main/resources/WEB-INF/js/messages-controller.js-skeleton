/**
 * de.sb.messenger.MessagesController: messenger messages controller.
 * Copyright (c) 2013 Sascha Baumeister
 */
"use strict";

this.de_sb_messenger = this.de_sb_messenger || {};
(function () {
	var SUPER = de_sb_messenger.Controller;

	/**
	 * Creates a new messages controller that is derived from an abstract controller.
	 * @param sessionContext {de_sb_messenger.SessionContext} a session context
	 * @param entityCache {de_sb_messenger.EntityCache} an entity cache
	 */
	de_sb_messenger.MessagesController = function (sessionContext, entityCache) {
		SUPER.call(this, 1, sessionContext, entityCache);
	}
	de_sb_messenger.MessagesController.prototype = Object.create(SUPER.prototype);
	de_sb_messenger.MessagesController.prototype.constructor = de_sb_messenger.MessagesController;

	/**
	 * Displays the associated view by calling the supertype's display
	 * method implementation and expanding it.
	 */
	de_sb_messenger.MessagesController.prototype.display = function () {
		if (!this.sessionContext.user) return;
		SUPER.prototype.display.call(this);

		var subjectIdentities = [this.sessionContext.user.identity].concat(this.sessionContext.user.observedReferences);
		var mainElement = document.querySelector("main");
		var subjectsElement = document.querySelector("#subjects-template").content.cloneNode(true).firstElementChild;
		var messagesElement = document.querySelector("#messages-template").content.cloneNode(true).firstElementChild;
		mainElement.appendChild(subjectsElement);
		mainElement.appendChild(messagesElement);

		this.refreshAvatarSlider(subjectsElement.querySelector("div.image-slider"), subjectIdentities, this.displayMessageEditor.bind(this, messagesElement));
		this.displayRootMessages();
	}


	/**
	 * Displays the root messages.
	 */
	de_sb_messenger.MessagesController.prototype.displayRootMessages = function () {
		// TODO
	}


	/**
	 * Discards an existing message editor if present, and displays a new one
	 * for the given subject.
	 * @param parentElement {Element} the parent element
	 * @param subjectIdentity {String} the subject identity
	 */
	de_sb_messenger.MessagesController.prototype.displayMessageEditor = function (parentElement, subjectIdentity) {
		// TODO
	}
} ());