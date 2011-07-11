package com.gemserk.games.facehunt.components;

import com.artemis.Component;

public class ScriptComponent extends Component {

	private Script script;

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

	public ScriptComponent(Script script) {
		this.script = script;
	}

}
