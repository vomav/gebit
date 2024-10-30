import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import JSONModel from "sap/ui/model/json/JSONModel";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Dialog from "sap/m/Dialog";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class TerritoryDetail extends Controller {

	assigTerritoryToUserDialog:Dialog;
	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "territoryDetail") {
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

	public formatLinkToEmbedHtml(link:string) {
		let escapedLink = link.replace("&", "&amp;")
		// return "&lt;iframe src=&quot;" + escapedLink + "&quot width=&quot;100%&quot; height=&quot;480&quot;&gt;&lt;/iframe&gt;"
		return "<iframe src=\"" +link+ "\" width=\"100%\" height=\"480\"></iframe>"
	}

	public onDeleteTerritory(oEvent:Event) {
		let model = (this.getView()?.getModel() as ODataModel);

		let bindingPath = this.getView().getBindingContext().getPath();
		if(bindingPath) 
			model.delete(bindingPath);

		(this.getOwnerComponent() as UIComponent).getRouter().navTo("territories");
	}

	 public async onAssignTerritoryToUserPress(oEvent:Event) {
		let context = await model.bindContext("assignToUser(...)");

	}

	public onOpenDialogAssignTerritoryToUserPress (oEvent:Event) {

		let oView = this.getView();
		let that = this;
		if (!this.assigTerritoryToUserDialog) {
			this.loadFragment({name:"ui5.gebit.app.fragment.AssignTerritoryToUser", addToDependents: true}).then(function(dialog:any){
				that.assigTerritoryToUserDialog = dialog as Dialog;
				oView?.addDependent(that.assigTerritoryToUserDialog);
				that.assigTerritoryToUserDialog.bindElement({
					path: "/territory/assignTerritoryToUser",
					model:"uiModel"
				});
				that.assigTerritoryToUserDialog.open();
				return that.assigTerritoryToUserDialog;
			});
		} else {
			that.assigTerritoryToUserDialog.open();
		}
		// this.assigTerritoryToUserDialog.then(function(oDialog) {
		// 	// Create a filter for the binding
		// 	// oDialog.getBinding("items").filter([new Filter("Name", FilterOperator.Contains, sInputValue)]);
		// 	// Open ValueHelpDialog filtered by the input's value
		// 	// oDialog.open(sInputValue);
		// });
	}
	public onValueHelpClose (oEvent:Event) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/territory/assignTerritoryToUser", { 
			input : "",
            selectedUser : {}
		});
	}

	public onValueHelpSearch(oEvent:any) {

	} 

	public onSelectUser(oEvent:any) {
		let userId = oEvent.getParameter("selectedItem").getBindingContext().getObject().user_ID;

	}
}