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
/**
 * @namespace ui5.gebit.app.controller
 */
export default class GroupTerritoryDetail extends Controller {

	usersDialog: Dialog;
	currentPartsContextBinding: Context;
	currentCoordinates:[];
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
		(this.getView() as any).bindElement("/TerritoryAssignments(" + context + ")");

	}

	public onActionButtonPress(oEvent: Event) {
		let oButton = oEvent.getSource() as Button;
		this.currentPartsContextBinding = oButton.getBindingContext() ? oButton.getBindingContext():null;
		let i18nModel = this.getView()?.getModel("i18n");
		let actionSheet = this.getView()?.byId("myPartsactionSheet") as ActionSheet;
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
				this.currentPartsContextBinding
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
		let isDoneFilter = new Filter("isDone", FilterOperator.NE, true);
		let sortByName = new Sorter("name", false);
		
		switch (sKey) {
			case "All":
				aSorters.push(sortByName);
				break;
			
			case "Available":
				aFilters.push(isDoneFilter);
				aSorters.push(sortByName);
				break;
	
			case "CloseToMe":
			aFilters.push(isDoneFilter);

			// let sort = new Sorter("coordinates", false, this.sortByPossition);
				navigator.geolocation.getCurrentPosition(this.onCurrentCoordinatesReceived.bind(this));
				// oBinding.sort([sort]);
				// aSorters.push(sort);
				break;

			default:
				break;
		}

		oBinding.filter(new Filter(aFilters));
		oBinding.sort(aSorters);
		


	}

	public sortByPossition(value1, value2) {
		console.log("value1 " + value1.getObject());
		console.log("value2 " + value2.getObject());
		return 0;
	}

	public onCurrentCoordinatesReceived(position:any) {
		let oBinding = this.getView()?.byId("groupTerrirtoryParts")?.getBinding("items") as ODataListBinding;
		

		
		Sorter.defaultComparator = (a:any,b:any) =>{
			console.log("value1 " + a.getObject());
			console.log("value2 " + b.getObject());
			return 0;
		};


		// let sort = new Sorter("coordinates", false, false, function(value1:any, value2:any) {
		// 	console.log("value1 " + value1.getObject());
		// 	console.log("value2 " + value2.getObject());
		// 	return 0;
		// });

		let sort = new  Sorter({
			path: "coordinates",
			descending: false
		});

		sort.defaultComparator =function(value1:any, value2:any) {
				console.log("value1 " + value1.getObject());
				console.log("value2 " + value2.getObject());
				return 0;
			};
		let lat = position.coords.latitude;
		let long = position.coords.longitude;

		let coords = [lat, long];
		this.getView().data("coords", coords)

		oBinding.sort([sort]);
	
		console.log(lat + " : " + long);
		// this.currentCoordinates.push(lat);
		// this.currentCoordinates.push(long);
	}
}