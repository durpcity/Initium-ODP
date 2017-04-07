package com.universeprojects.miniup.server.commands;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.universeprojects.cacheddatastore.CachedDatastoreService;
import com.universeprojects.cacheddatastore.CachedEntity;
import com.universeprojects.miniup.CommonChecks;
import com.universeprojects.miniup.server.GameUtils;
import com.universeprojects.miniup.server.ODPDBAccess;
import com.universeprojects.miniup.server.UserRequestIncompleteException;
import com.universeprojects.miniup.server.commands.framework.Command;
import com.universeprojects.miniup.server.commands.framework.UserErrorMessage;

public class CommandCharacterCollectCharacter extends Command {

	public CommandCharacterCollectCharacter(ODPDBAccess db,
			HttpServletRequest request, HttpServletResponse response) {
		super(db, request, response);
	}

	@Override
	public void run(Map<String, String> parameters) throws UserErrorMessage,
			UserRequestIncompleteException {
		ODPDBAccess db = getDB();
		CachedEntity character = db.getCurrentCharacter();
		CachedEntity user = db.getCurrentUser();
		
		CachedEntity pickupChar = null;
		
		if(parameters.get("characterId") != null)
		{
			try
			{
				Long characterId = tryParseId(parameters, "characterId");
				pickupChar = db.getCharacterById(characterId);
			}
			catch(Exception ex)
			{
				throw new RuntimeException("characterId parameter is malformed", ex);
			}
		}
		else if(parameters.containsKey("characterName"))
		{
			String characterName = parameters.get("characterName");
			pickupChar = db.getCharacterByName(characterName);
		}
		else
			throw new RuntimeException("Invalid parameters passed to command!");
		
		if(pickupChar == null)
			throw new UserErrorMessage("That character doesn't seem to exist...");
		
		// This will put to DB.
		db.doCharacterCollectCharacter(user, character, pickupChar);
	}

}
