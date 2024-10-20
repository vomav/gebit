import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import Control from "sap/ui/core/Control";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import JSONModel from "sap/ui/model/json/JSONModel";
/**
 * @namespace ui5.gebit.app.reuse.territories.controller
 */           
export default class Details extends Controller {

	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "detail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		(this.getView() as any).bindElement("/Territories("+context+")");
		
	}

	public onEditButton(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", true);
	}

	public onDisplayMode(oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/ui/editMode", false);
	}

	/// &lt;iframe src=&quot;https://www.google.com/maps/d/embed?mid=1lZtqJd0cii19y2ioV9EH7z60tlQTSGo&amp;ehbc=2E312F&quot; width=&quot;100%&quot; height=&quot;480&quot;&gt;&lt;/iframe&gt;

	public formatLinkToEmbedHtml(link:string) {
		let escapedLink = link.replace("&", "&amp;")
		// return "&lt;iframe src=&quot;" + escapedLink + "&quot width=&quot;100%&quot; height=&quot;480&quot;&gt;&lt;/iframe&gt;"
		return "<iframe src=\"" +link+ "\" width=\"100%\" height=\"480\"></iframe>"
	}

	public formatCoordinates(coordnates:string) {
		return coordnates;
	} 
}