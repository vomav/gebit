import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import Dialog from "sap/m/Dialog";
import Filter from "sap/ui/model/Filter";
import FilterOperator from "sap/ui/model/FilterOperator";
import MessageBox from "sap/m/MessageBox";
import Context from "sap/ui/model/odata/v4/Context";
import ActionSheet from "sap/m/ActionSheet";
import Button from "sap/m/Button";
import MessageToast from "sap/m/MessageToast";
import Sorter from "sap/ui/model/Sorter";
import ODataListBinding from "sap/ui/model/odata/v4/ODataListBinding";
import Table from "sap/m/Table";
import CustomData from "sap/ui/core/CustomData";
/**
 * @namespace ui5.gebit.app.controller
 */
export default class GroupTerritoryDetail extends Controller {

	usersDialog: Dialog;
	currentPartsContextBinding: Context;
	public onInit(): void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent: Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if (routeName == "groupTerritoryDetail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

 	public matched(context: string) {
		(this.getView() as any).bindElement("/PublicTerritoryAssignments(" + context + ")");

	}

	public onActionButtonPress(oEvent: Event) {
		let oButton = oEvent.getSource() as Button;
		this.currentPartsContextBinding = oButton.getBindingContext() ? oButton.getBindingContext():null;
		let i18nModel = this.getView()?.getModel("i18n");
		let actionSheet = oButton.getDependents()[0] as ActionSheet;
		actionSheet?.setModel(i18nModel, "i18n");
		actionSheet.openBy(oButton);

	}

	public async assignPartToMe(oEvent: Event) {
		let model = (this.getView()?.getModel() as ODataModel);
		if (this.currentPartsContextBinding) {
			let context = await model.bindContext("srv.searching.assignPartToMe(...)", this.currentPartsContextBinding);
			context.execute().then(function () {
				MessageToast.show("{i18n>ok}");
				this.getView()?.getModel().refresh();
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
			);
		}
	}

	public assignPartToUser(oEvent: Event) {
		let that = this;
		if (this.usersDialog == null) {
			this.loadFragment({ name: "ui5.gebit.app.fragment.AssignPartToUser", addToDependents: true }).then(function (dialog: any) {
				that.usersDialog = dialog as Dialog;
				that.usersDialog.open();
			}.bind(this));
		} else {
			that.usersDialog.open();
		}
	}

	public async cancelPartAssignment(oEvent: Event) {
		let model = (this.getView()?.getModel() as ODataModel);
		let context = await model.bindContext("srv.searching.cancelPartAssignment(...)", this.currentPartsContextBinding);
		context.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			this.getView()?.getModel().refresh();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}
		);
	}

	public async onSelectUser(oEvent: Event) {
		let userId = oEvent.getParameter("selectedItem").getBindingContext().getObject().user_ID;
		let model = (this.getView()?.getModel() as ODataModel);
		if (this.currentPartsContextBinding) {
			let context = await model.bindContext("srv.searching.assignPartToUser(...)", this.currentPartsContextBinding);
			context.setParameter("userId", userId);
			context.execute().then(function () {
				MessageToast.show("{i18n>ok}");
				this.getView()?.getModel().refresh();
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
			);
		}
	}

	public onValueHelpClose(oEvent: Event) {
		this.usersDialog.clone();
	}

	public async onFilterSelect (oEvent:Event) {
		
		let sKey = oEvent.getParameter("key");
		let aFilters = [];
		let aSorters = [] as Sorter[];
		let oBinding = this.getView()?.byId("groupTerrirtoryParts")?.getBinding("items") as ODataListBinding;
		let sortByName = new Sorter("name", false);
		
		switch (sKey) {
			case "All":
				aSorters.push(sortByName);
				break;
			
			case "Available":
				aFilters.push(new Filter("userName", FilterOperator.EQ, null));
				aSorters.push(sortByName);
				break;
	
			case "CloseToMe":
				break;

			case "InProgress": 
				let isNotDone = new Filter([new Filter("isDone", FilterOperator.EQ, null), new Filter("isDone", FilterOperator.EQ, false)], false);
				let isAssigned = new Filter([new Filter("userName", FilterOperator.NE, null), isNotDone], true);
				// aFilters.push(isNotDone);
				aFilters.push(isAssigned);
				aSorters.push(sortByName);
				break;
			default:
				break;
		}

		oBinding.filter(new Filter(aFilters));
		oBinding.sort(aSorters);
		


	}

	public formatLinkToEmbedHtml(link:string) {
		let escapedLink = link.replace("&", "&amp;")
		// return "&lt;iframe src=&quot;" + escapedLink + "&quot width=&quot;100%&quot; height=&quot;480&quot;&gt;&lt;/iframe&gt;"
		return "<iframe src=\"" +link+ "\" width=\"100%\" height=\"480\"></iframe>"
	}
	// public onCurrentCoordinatesReceived(position:any) {
	// 	let table = this.getView()?.byId("groupTerrirtoryParts") as Table;
	// 	let oBinding = table?.getBinding("items") as ODataListBinding;
		
	// 	let lat = position.coords.latitude;
	// 	let long = position.coords.longitude;

	
	// 	table.getItems().forEach(item => {
	// 		let coordinates = item.getBindingContext()?.getObject();
	// 		// '[[8.6870917,49.4063731],[8.6871132,49.4034408],[8.6871239,49.4019538],[8.6872848,49.4004946],[8.6868127,49.4004597],[8.6887654,49.3991261],[8.6895379,49.3992239],[8.6901387,49.3990703],[8.6907395,49.3991122],[8.6917909,49.3996009],[8.6927994,49.3984838],[8.6930998,49.3979252],[8.6938723,49.3975761],[8.694516,49.3970175],[8.6952242,49.396822],[8.6973699,49.3967102],[8.6977132,49.4016955],[8.6969622,49.4042437],[8.6941942,49.4075459],[8.6870917,49.4063731]]'

	// 		// long : 8.5717693
	// 		// lat : 49.3848141
			
	// 		let cd = new CustomData({key:"proximity", value: Math.round(Math.random() * 100)})
	// 		item.removeCustomData("proximity");
	// 		item.addCustomData(cd);
	// 	});
		
	// 	oBinding.sort([new Sorter("")]);
	// 	console.log(lat + " : " + long);
	// }
}