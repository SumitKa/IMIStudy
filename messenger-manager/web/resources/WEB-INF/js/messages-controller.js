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
	 * @param entityCache {de_sb_messenger.EntityCache} an entity cache
	 */
	de_sb_messenger.MessagesController = function (entityCache) {
		SUPER.call(this, 1, entityCache);
	}
	de_sb_messenger.MessagesController.prototype = Object.create(SUPER.prototype);
	de_sb_messenger.MessagesController.prototype.constructor = de_sb_messenger.MessagesController;

	/**
	 * Displays the associated view by calling the supertype's display
	 * method implementation and expanding it.
	 */
	de_sb_messenger.MessagesController.prototype.display = function () {
        var sessionUser = de_sb_messenger.APPLICATION.sessionUser;
        if (!sessionUser) return;

		SUPER.prototype.display.call(this);

		var subjectIdentities = [sessionUser.identity].concat(sessionUser.observedReferences);
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
        var sessionUser = de_sb_messenger.APPLICATION.sessionUser;
        if (!sessionUser) return;

        SUPER.prototype.display.call(this);
        this.displayStatus(200, "OK");

        var sectionElement = document.querySelector("#preferences-template").content.cloneNode(true).firstElementChild;
        sectionElement.querySelector("button").addEventListener("click", this.persistUser.bind(this));
        document.querySelector("main").appendChild(sectionElement);

        var self = this;
        var imageElement = sectionElement.querySelector("img");
        imageElement.dropFile = null;
        imageElement.addEventListener("dragover", function (event) {
            (event = event || window.event).preventDefault();
            event.dataTransfer.dropEffect = "copy";
        });
        imageElement.addEventListener("drop", function (event) {
            (event = event || window.event).preventDefault();
            if (event.dataTransfer.files.length === 0) return;
            this.dropFile = event.dataTransfer.files[0];
            this.src = URL.createObjectURL(this.dropFile);
            self.persistAvatar();
        });
        imageElement.addEventListener("load", function (event) {
            (event = event || window.event).preventDefault();
            URL.revokeObjectURL(this.src);
        });

        this.displayUser();
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