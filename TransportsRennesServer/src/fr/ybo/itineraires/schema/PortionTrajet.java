
package fr.ybo.itineraires.schema;

public final class PortionTrajet {

    protected JointureCorrespondance jointureCorrespondance;
    protected JointurePieton jointurePieton;
    protected PortionTrajetBus portionTrajetBus;

    public JointureCorrespondance getJointureCorrespondance() {
        return jointureCorrespondance;
    }

    public void setJointureCorrespondance(JointureCorrespondance jointureCorrespondance) {
        this.jointureCorrespondance = jointureCorrespondance;
    }

    public JointurePieton getJointurePieton() {
        return jointurePieton;
    }

    public void setJointurePieton(JointurePieton jointurePieton) {
        this.jointurePieton = jointurePieton;
    }

    public PortionTrajetBus getPortionTrajetBus() {
        return portionTrajetBus;
    }

    public void setPortionTrajetBus(PortionTrajetBus portionTrajetBus) {
        this.portionTrajetBus = portionTrajetBus;
    }
}
